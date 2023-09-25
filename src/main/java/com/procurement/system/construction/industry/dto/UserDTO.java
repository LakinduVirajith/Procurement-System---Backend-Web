package com.procurement.system.construction.industry.dto;

import com.procurement.system.construction.industry.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String email;

    @NotNull
    private String mobileNumber;

    @NotNull
    private String password;

    @NotNull
    private UserRole role;

    @Builder.Default
    private Boolean isActive = false;
}
