package com.procurement.system.construction.industry.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {

    private Long itemId;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private String manufacturer;

    @NotNull
    private Integer quantityAvailable;

    @NotNull
    private Double price;

    @NotNull
    private String volumeType;

    private Double weight;

    private String color;
}
