package com.procurement.system.construction.industry.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteDTO {

    private Long siteId;

    @NotNull
    private String siteName;

    @NotNull
    private String location;

    private LocalDate startDate;

    @NotNull
    private String contactNumber;

    private double allocatedBudget;

    private Long siteManagerId;

    private Long procurementManagerId;
}
