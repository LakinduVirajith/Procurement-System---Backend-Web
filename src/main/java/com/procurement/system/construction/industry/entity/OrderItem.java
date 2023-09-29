package com.procurement.system.construction.industry.entity;

import com.procurement.system.construction.industry.enums.Status;
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
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "oi_item_id")
    private Item item;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "oi_order_id")
    private OrderDetails order;
}
