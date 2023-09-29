package com.procurement.system.construction.industry.common;

import com.procurement.system.construction.industry.config.jwt.JwtService;
import com.procurement.system.construction.industry.entity.User;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonFunctions {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static String token;
    public void storeJWT(String jwt) {
        token = jwt;
    }

    public String getToken() throws NotFoundException {
        if(!token.isEmpty()){
            return token;
        }else{
            throw new NotFoundException("Oops! It looks like the token is missing");
        }
    }

    public String getUserEmail(){
        return jwtService.extractUsername(token);
    }

    public Long getUserId() throws NotFoundException {
        Optional<User> user = userRepository.findByEmail(getUserEmail());

        if(user.isPresent()){
            return user.get().getUserId();
        }else{
            throw new NotFoundException("Oops! We couldn't find this account");
        }
    }

    public User getUser() throws NotFoundException {
        Optional<User> user = userRepository.findByEmail(getUserEmail());

        if(user.isPresent()){
            return user.get();
        }else{
            throw new NotFoundException("Oops! We couldn't find this account");
        }
    }

    public ResponseEntity<ResponseMessage> successResponse(String message){
        ResponseMessage successResponse = new ResponseMessage();
        successResponse.setStatusCode(200);
        successResponse.setStatus(HttpStatus.OK);
        successResponse.setMessage(message);

        return ResponseEntity.ok().body(successResponse);
    }
}
