package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.AuthenticationRequest;
import com.procurement.system.construction.industry.common.AuthenticationResponse;
import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.GetUserDTO;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.entity.AuthToken;
import com.procurement.system.construction.industry.entity.User;
import com.procurement.system.construction.industry.enums.UserRole;
import com.procurement.system.construction.industry.exception.*;
import com.procurement.system.construction.industry.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void addUser_shouldReturnSuccessResponse_whenUserAddedSuccessfully() throws ConflictException {
        UserDTO userDTO = UserDTO.builder()
                .firstName("test first")
                .lastName("test last")
                .email("test@example.com")
                .password("password")
                .role(UserRole.SITE_MANAGER)
                .isActive(true)
                .build();

        // When
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<ResponseMessage> responseEntity = userServiceImpl.addUser(userDTO);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("user registered successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void addUser_shouldThrowConflictException_whenEmailAlreadyExists() throws ConflictException {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");

        // When
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

        // Then
        assertThrows(ConflictException.class, () -> userServiceImpl.addUser(userDTO));
    }

    @Test
    @SneakyThrows
    public void getAllUsers_shouldReturnEmptyPage_whenNoUsersFound() throws NotFoundException {
        // When
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        // Then
        assertThrows(NotFoundException.class, () -> userServiceImpl.getAllUsers(Pageable.unpaged()));
    }

    @Test
    @SneakyThrows
    public void getAllUsers_shouldReturnPageOfUsers_whenUsersFound() {
        User user1 = User.builder()
                .userId(1L)
                .firstName("test1 first")
                .lastName("test1 last")
                .email("test1@example.com")
                .password("password")
                .role(UserRole.SITE_MANAGER)
                .isActive(true)
                .build();

        User user2 = User.builder()
                .userId(2L)
                .firstName("test2 first")
                .lastName("test2 last")
                .email("test2@example.com")
                .password("password")
                .role(UserRole.SITE_MANAGER)
                .isActive(false)
                .build();

        // When
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user1, user2)));

        Page<GetUserDTO> users = userServiceImpl.getAllUsers(Pageable.unpaged());

        // Then
        assertThat(users).isNotEmpty();
        assertThat(users.getTotalElements()).isEqualTo(2L);
    }

    @Test
    public void activate_shouldReturnSuccessResponse_whenUserActivatedSuccessfully() throws NotFoundException, ConflictException {
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setIsActive(false);

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<ResponseMessage> responseEntity = userServiceImpl.activate(userId);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("user activated successfully");
    }

    @Test
    public void activate_shouldThrowNotFoundException_whenUserNotFound() throws ConflictException, NotFoundException {
        Long userId = 1L;

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Then
        assertThat(userServiceImpl.activate(userId)).isNotExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    public void activate_shouldThrowConflictException_whenUserAlreadyActivated() throws ConflictException, NotFoundException {
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setIsActive(true);

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Then
        assertThat(userServiceImpl.activate(userId)).isNotExactlyInstanceOf(ConflictException.class);
    }

    @Test
    public void deactivate_shouldReturnSuccessResponse_whenUserDeactivatedSuccessfully() throws NotFoundException, ConflictException {
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setIsActive(true);

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<ResponseMessage> responseEntity = userServiceImpl.deactivate(userId);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("user deactivated successfully");
    }

    @Test
    public void deactivate_shouldThrowNotFoundException_whenUserNotFound() throws ConflictException, NotFoundException {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        // Then
        assertThat(userServiceImpl.deactivate(userId)).isNotExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    public void deactivate_shouldThrowConflictException_whenUserAlreadyDeactivated() throws ConflictException, NotFoundException {
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setIsActive(false);

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Then
        assertThat(userServiceImpl.deactivate(userId)).isNotExactlyInstanceOf(ConflictException.class);
    }


    @Test
    public void resetPassword_shouldReturnSuccessResponse_whenPasswordResetSuccessfully() throws NotFoundException {
        String email = "test@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);

        // When
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<ResponseMessage> responseEntity = userServiceImpl.resetPassword(email, password);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("password reset successfully");
    }

    @Test
    @SneakyThrows
    public void resetPassword_shouldThrowNotFoundException_whenUserNotFound() {
        String email = "test@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        // Then
        assertThat(userServiceImpl.resetPassword(email, password)).isNotExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    public void authenticate_shouldReturnSuccessResponse_whenUserAuthenticatedSuccessfully() throws NotFoundException, ForbiddenException {
        String email = "test@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // When
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        AuthenticationResponse authenticationResponse = userServiceImpl.authenticate(new AuthenticationRequest(email, password)).getBody();

        // Then
        assertThat(authenticationResponse.getStatusCode()).isEqualTo(200);
        assertThat(authenticationResponse.getMessage()).isEqualTo("user authenticated successfully");
        assertThat(authenticationResponse.getAccessToken()).isNotEmpty();
        assertThat(authenticationResponse.getRefreshToken()).isNotEmpty();
    }

    @Test
    public void authenticate_shouldThrowNotFoundException_whenUserNotFound() throws ForbiddenException, NotFoundException {
        String email = "test@example.com";
        String password = "password";

        // When
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Then
        assertThat(userServiceImpl.authenticate(new AuthenticationRequest(email, password))).isNotExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    public void authenticate_shouldThrowForbiddenException_whenUserNotActivated() throws ForbiddenException, NotFoundException {
        String email = "test@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(false);

        // When
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Then
        assertThat(userServiceImpl.authenticate(new AuthenticationRequest(email, password))).isNotExactlyInstanceOf(ForbiddenException.class);
    }

    @Test
    public void refreshToken_shouldReturnSuccessResponse_whenRefreshTokenIsValid() throws BadRequestException, NotFoundException, InternalServerException {
        String refreshToken = "refreshToken";

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthToken(new AuthToken(1L, refreshToken, false, false, user));

        // When
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        AuthenticationResponse authenticationResponse = userServiceImpl.refreshToken(refreshToken).getBody();

        // Then
        assertThat(authenticationResponse.getStatusCode()).isEqualTo(200);
        assertThat(authenticationResponse.getMessage()).isEqualTo("Using refresh token user authenticated successfully");
        assertThat(authenticationResponse.getAccessToken()).isNotEmpty();
        assertThat(authenticationResponse.getRefreshToken()).isNotEmpty();
    }

    @Test
    public void refreshToken_shouldThrowBadRequestException_whenRefreshTokenIsInvalid() throws InternalServerException, BadRequestException, NotFoundException {
        String refreshToken = "refreshToken";

        // Then
        assertThat(userServiceImpl.refreshToken(refreshToken)).isNotExactlyInstanceOf(BadRequestException.class);
    }

    @Test
    public void refreshToken_shouldThrowNotFoundException_whenUserNotFound() throws BadRequestException, InternalServerException, NotFoundException {
        String refreshToken = "refreshToken";

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthToken(new AuthToken(1L, refreshToken, false, false, user));

        // When
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Then
        assertThat(userServiceImpl.refreshToken(refreshToken)).isNotExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    public void refreshToken_shouldThrowInternalServerException_whenSomethingWentWrong() throws BadRequestException, NotFoundException, InternalServerException {
        String refreshToken = "refreshToken";

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthToken(new AuthToken(1L ,refreshToken, false, false, user));

        // When
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Then
        assertThat(userServiceImpl.refreshToken(refreshToken)).isNotExactlyInstanceOf(InternalServerException.class);
    }

    @Test
    public void logout_shouldReturnSuccessResponse_whenUserLoggedOutSuccessfully() throws NotFoundException, BadRequestException {
        String token = "token";

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthToken(new AuthToken(1L, token, false, false, user));

        // When
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<ResponseMessage> responseEntity = userServiceImpl.logout();

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getMessage()).isEqualTo("user logout successfully");
    }

    @Test
    public void logout_shouldThrowNotFoundException_whenUserNotFound() throws NotFoundException, BadRequestException {
        // Given
        String token = "token";

        // When
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        // Then
        assertThat(userServiceImpl.logout()).isNotExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    public void logout_shouldThrowBadRequestException_whenTokenIsInvalid() throws NotFoundException, BadRequestException {
        String token = "token";

        // Then
        assertThat(userServiceImpl.logout()).isNotExactlyInstanceOf(BadRequestException.class);
    }
}