package com.procurement.system.construction.industry.dto;

import com.procurement.system.construction.industry.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private Long orderItemId;

    @NotNull
    private int quantity;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long itemId;

    private Long orderId;
}
