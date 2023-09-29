package com.procurement.system.construction.industry.entity;

import com.procurement.system.construction.industry.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private LocalDateTime requiredDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @OneToOne
    @JoinColumn(name = "supplier_id")
    private User supplier;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "o_site_id")
    private Site site;
}
