package com.procurement.system.construction.industry.repository;

import com.procurement.system.construction.industry.entity.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    Page<Site> findAll(Pageable pageable);
}
