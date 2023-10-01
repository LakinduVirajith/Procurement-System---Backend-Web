package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.AuthenticationRequest;
import com.procurement.system.construction.industry.common.AuthenticationResponse;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.exception.*;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<ResponseMessage> addUser(UserDTO userDTO) throws ConflictException;

    ResponseEntity<ResponseMessage> activate(Long userId) throws NotFoundException, ConflictException;

    ResponseEntity<ResponseMessage> deactivate(Long userId) throws NotFoundException, ConflictException;

    ResponseEntity<ResponseMessage> restPassword(String email, String password) throws NotFoundException;

    ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) throws NotFoundException, ForbiddenException;

    ResponseEntity<AuthenticationResponse> refreshToken(String refreshToken) throws BadRequestException, NotFoundException, InternalServerException;

    ResponseEntity<ResponseMessage> logout() throws NotFoundException, BadRequestException;
}
