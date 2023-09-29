package com.procurement.system.construction.industry.controller;

import com.procurement.system.construction.industry.common.AuthenticationRequest;
import com.procurement.system.construction.industry.common.AuthenticationResponse;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.exception.*;
import com.procurement.system.construction.industry.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "User Controllers")
public class UserController {

    private final UserService userService;

    // ADMIN ACCESS
    @Operation(summary = "User Registration", description = "Register a new user. Providing necessary details to create a user account.")
    @PostMapping("admin/register")
    public ResponseEntity<ResponseMessage> register(@Valid @RequestBody UserDTO userDTO) throws ConflictException {
        return userService.register(userDTO);
    }

    @Operation(summary = "Activate User Account", description = "Activate a user account using an userId")
    @GetMapping("admin/activate/{id}")
    public ResponseEntity<ResponseMessage> userActivate(@PathVariable("id") Long userId) throws ConflictException, NotFoundException {
        return userService.activate(userId);
    }

    @Operation(summary = "Deactivate Account", description = "Deactivate a user account using an userId")
    @PutMapping("admin/deactivate/{id}")
    public ResponseEntity<ResponseMessage> userDeactivate(@PathVariable("id") Long userId) throws ConflictException, NotFoundException {
        return userService.deactivate(userId);
    }

    @Operation(summary = "Reset Password", description = "Initiate password reset by Providing necessary details.")
    @PostMapping("admin/reset-password")
    public ResponseEntity<ResponseMessage> restPassword(String email, String password) throws NotFoundException {
        return userService.restPassword(email, password);
    }

    // GLOBAL ACCESS
    @Operation(summary = "User Authentication", description = "Authenticate a user by providing valid credentials.")
    @PostMapping("user/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws ForbiddenException, NotFoundException {
        return userService.authenticate(request);
    }

    @Operation(summary = "Refresh Access Token", description = "Refresh the access token by providing a valid refresh token. This endpoint allows you to obtain a new access token using a valid refresh token, which helps in maintaining user authentication without requiring the user to log in again.")
    @PostMapping("user/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(String refreshToken) throws InternalServerException, BadRequestException, NotFoundException {
        return userService.refreshToken(refreshToken);
    }

    // ALL USER ACCESS
    @Operation(summary = "Logout", description = "Invalidate the user's authentication token to log out.")
    @PutMapping("all-users/logout")
    public ResponseEntity<ResponseMessage> logout() throws NotFoundException, BadRequestException {
        return userService.logout();
    }
}
