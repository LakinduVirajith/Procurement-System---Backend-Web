package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.AuthenticationRequest;
import com.procurement.system.construction.industry.common.AuthenticationResponse;
import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.config.jwt.JwtService;
import com.procurement.system.construction.industry.dto.GetUserDTO;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.entity.AuthToken;
import com.procurement.system.construction.industry.entity.User;
import com.procurement.system.construction.industry.exception.*;
import com.procurement.system.construction.industry.repository.AuthTokenRepository;
import com.procurement.system.construction.industry.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CommonFunctions commonFunctions;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseMessage> addUser(UserDTO userDTO) throws ConflictException {
        Optional<User> emailCondition = userRepository.findByEmail(userDTO.getEmail());

        // EMAIL CONFLICT EXCEPTION
        if(emailCondition.isPresent()){
            throw new ConflictException("email already exists");
        }

        // ENCODE PASSWORD USING PASSWORD-ENCODER
        String encodedPassword = encodePassword(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);

        // DEFAULT VALUES
        if(userDTO.getIsActive() == null){
            userDTO.setIsActive(false);
        }

        User user = new User();
        modelMapper.map(userDTO, user);
        userRepository.save(user);

        return commonFunctions.successResponse("user registered successfully");
    }

    @Override
    public Page<GetUserDTO> getAllUsers(Pageable pageable) throws NotFoundException {
        Page<User> users  = userRepository.findAll(pageable);

        if(users .isEmpty()){
            throw new NotFoundException("no users have been found in the system");
        }

        return users.map(user -> modelMapper.map(user, GetUserDTO.class));
    }

    @Override
    public ResponseEntity<ResponseMessage> activate(Long userId) throws NotFoundException, ConflictException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("invalid user id"));

        // ALREADY ACTIVATED EXCEPTION
        if(user.getIsActive()){
            throw new ConflictException("user is already activated");
        }

        user.setIsActive(true);
        userRepository.save(user);

        return commonFunctions.successResponse("user activated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> deactivate(Long userId) throws NotFoundException, ConflictException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("invalid user id"));

        // ALREADY ACTIVATED EXCEPTION
        if(!user.getIsActive()){
            throw new ConflictException("user is already deactivated");
        }

        user.setIsActive(false);
        userRepository.save(user);

        return commonFunctions.successResponse("user deactivated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> resetPassword(String email, String password) throws NotFoundException {
        Optional<User> userCondition = userRepository.findByEmail(email);

        // INVALID USER EXCEPTION
        if(userCondition.isEmpty()){
            throw new NotFoundException("provided email address is invalid");
        }
        User user = userCondition.get();

        // ENCODE PASSWORD USING PASSWORD-ENCODER
        String encodedPassword = encodePassword(password);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return commonFunctions.successResponse("password reset successfully");
    }

    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) throws NotFoundException, ForbiddenException {
        Optional<User> userCondition = userRepository.findByEmail(request.getEmail());

        // NOT FOUND EXCEPTION
        if(userCondition.isEmpty()){
            throw new NotFoundException("invalid user name");
        }

        // NOT ACTIVATE EXCEPTION
        if(!userCondition.get().getIsActive()){
            throw new ForbiddenException("your account is not activated yet.");
        }

        User user  = userCondition.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NotFoundException("invalid user password");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // GENERATE ACCESS-TOKEN
        var jwtToken = jwtService.generateToken(user);
        saveToken(user, jwtToken);

        // GENERATE REFRESH-TOKEN
        var refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok().body(AuthenticationResponse.builder()
                .statusCode(200).
                status(HttpStatus.OK).
                message("user authenticated successfully").
                userRole(user.getRole().name()).
                accessToken(jwtToken).
                refreshToken(refreshToken).build());
    }

    @Override
    public ResponseEntity<AuthenticationResponse> refreshToken(String refreshToken) throws BadRequestException, NotFoundException, InternalServerException {
        final String userEmail;
        userEmail = jwtService.extractUsername(refreshToken);

        // INVALID TOKEN EXCEPTION
        if(userEmail.isEmpty()){
            throw new BadRequestException("Sorry, the token you provided is invalid");
        }

        // INVALID ACCOUNT EXCEPTION
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isPresent() && !optionalUser.get().getIsActive()) {
            throw new BadRequestException("Your account is not activated yet");
        }

        // INVALID USER EXCEPTION
        var user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("Oops! We couldn't find this account"));

        // GENERATE TOKEN
        AuthenticationResponse authResponse = null;
        if (jwtService.isTokenValid(refreshToken, user)) {
            var accessToken = jwtService.generateToken(user);
            saveToken(user, accessToken);

            authResponse = AuthenticationResponse.builder().
                    statusCode(200).
                    status(HttpStatus.OK).
                    message("Using refresh token user authenticated successfully").
                    userRole(user.getRole().name()).
                    accessToken(accessToken).
                    refreshToken(refreshToken).build();
        }

        // SERVER ERROR EXCEPTION
        if(authResponse == null){
            throw new InternalServerException("Something went wrong with generating the token");
        }
        return ResponseEntity.ok().body(authResponse);
    }

    @Override
    public ResponseEntity<ResponseMessage> logout() throws NotFoundException, BadRequestException {
        var token = commonFunctions.getToken();
        Optional<AuthToken> optionalToken = authTokenRepository.findByToken(token);

        if(optionalToken.isPresent()){
            var existToken = optionalToken.get();
            existToken.setExpired(true);
            existToken.setRevoked(true);
            authTokenRepository.save(existToken);
        }else{
            throw new BadRequestException("invalid logout");
        }

        return commonFunctions.successResponse("user logout successfully");
    }

    private void saveToken(User user, String jwtToken) {
        Optional<AuthToken> OptionalToken = authTokenRepository.findByUserUserId(user.getUserId());

        AuthToken token;
        // IF ALREADY HAVE TOKEN ELSE NO ANY TOKEN
        if(OptionalToken.isPresent()){
            token = OptionalToken.get();
            token.setToken(jwtToken);
            token.setExpired(false);
            token.setRevoked(false);

            authTokenRepository.save(token);
        }else{
            token = AuthToken.builder().user(user).token(jwtToken).expired(false).revoked(false).build();
            user.setAuthToken(token);

            userRepository.save(user);
        }
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
