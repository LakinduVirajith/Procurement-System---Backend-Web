package com.procurement.system.construction.industry.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long siteId;

    @NotNull
    private String siteName;

    @NotNull
    private String location;

    private Date startDate;

    @NotNull
    private String contactNumber;

    private double allocatedBudget;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> orders;

    @OneToOne
    @JoinColumn(name = "site_manager_id")
    private User siteManager;

    @OneToOne
    @JoinColumn(name = "procurement_manager_id")
    private User procurementManager;
}
