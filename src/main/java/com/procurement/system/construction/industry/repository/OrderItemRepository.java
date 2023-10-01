package com.procurement.system.construction.industry.repository;

import com.procurement.system.construction.industry.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findFirstByOrderItemId(Long orderItemId);
}
