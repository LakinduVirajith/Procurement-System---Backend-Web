package com.procurement.system.construction.industry.dto;

import com.procurement.system.construction.industry.entity.Site;
import com.procurement.system.construction.industry.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long orderId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private Date requiredDate;

    private List<OrderItemDTO> items;

    private Long supplierId;

    private Long siteId;
}
