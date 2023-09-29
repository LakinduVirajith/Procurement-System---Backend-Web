package com.procurement.system.construction.industry.repository;

import com.procurement.system.construction.industry.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findBySiteSiteId(Long siteId);
}
