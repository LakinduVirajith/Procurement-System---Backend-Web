package com.procurement.system.construction.industry.repository;

import com.procurement.system.construction.industry.entity.Site;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    Page<Site> findAll(Pageable pageable);

    Optional<Site> findBySiteManagerUserId(Long siteManagerId);

    Optional<Site> findByProcurementManagerUserId(Long procurementManagerId);
}
