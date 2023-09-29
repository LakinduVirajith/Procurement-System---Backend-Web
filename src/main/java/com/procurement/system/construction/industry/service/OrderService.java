package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.OrderDTO;
import com.procurement.system.construction.industry.dto.OrderItemDTO;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.exception.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {

    // ALL USER ACCESS
    List<OrderDTO> getAllOrderDetails() throws NotFoundException;

    OrderDTO getOrderDetails(Long orderId) throws NotFoundException;

    List<OrderItemDTO> getOrderItems(Long orderId) throws NotFoundException;

    // SITE MANAGER ACCESS
    ResponseEntity<ResponseMessage> addOrder(OrderDTO orderDTO) throws NotFoundException;

    ResponseEntity<ResponseMessage> addOrderItem(OrderItemDTO orderItemDTO) throws NotFoundException;

    ResponseEntity<ResponseMessage> removeOrderItem(Long orderItemId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsComplete(Long orderId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsCompleteItem(Long orderItemId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsReturnItem(Long orderItemId) throws NotFoundException;

    // PROCUREMENT MANAGER ACCESS
    ResponseEntity<ResponseMessage> assignSupplier(Long orderId, Long supplierId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsApproved(Long orderId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsCanceled(Long orderId) throws NotFoundException;

    // SUPPLIER ACCESS
    ResponseEntity<ResponseMessage> setAsDelivered(Long orderId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsDeliveredItem(Long orderItemId) throws NotFoundException;

    SiteDTO getSiteInfo(Long siteId) throws NotFoundException;

}
