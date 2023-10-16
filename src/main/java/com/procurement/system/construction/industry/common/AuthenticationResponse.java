package com.procurement.system.construction.industry.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private Integer statusCode;

    private HttpStatus status;

    private String message;

    private String userRole;

    private String accessToken;

    private String refreshToken;
}
