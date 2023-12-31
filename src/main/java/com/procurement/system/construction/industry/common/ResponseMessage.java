package com.procurement.system.construction.industry.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {
    private Integer statusCode;

    private HttpStatus status;

    private String message;
}
