package com.procurement.system.construction.industry.dto;

import com.procurement.system.construction.industry.entity.User;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    private Date startDate;

    @NotNull
    private String contactNumber;

    private double allocatedBudget;

    private Long siteManagerId;

    private Long procurementManagerId;
}
