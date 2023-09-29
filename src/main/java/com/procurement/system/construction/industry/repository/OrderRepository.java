package com.procurement.system.construction.industry.repository;

import com.procurement.system.construction.industry.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findBySiteSiteId(Long siteId);
}
