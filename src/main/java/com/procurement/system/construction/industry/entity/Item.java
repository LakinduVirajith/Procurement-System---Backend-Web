package com.procurement.system.construction.industry.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderItem orderItems;
}
