package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.GetUserDTO;
import com.procurement.system.construction.industry.dto.OrderDetailsDTO;
import com.procurement.system.construction.industry.dto.OrderItemDTO;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {

    // ALL USER ACCESS
    List<OrderDetailsDTO> getAllOrderDetails() throws NotFoundException;

    // SITE MANAGER ACCESS
    ResponseEntity<ResponseMessage> addOrder(OrderDetailsDTO orderDTO) throws NotFoundException;

    ResponseEntity<ResponseMessage> addOrderItem(OrderItemDTO orderItemDTO) throws NotFoundException;

    ResponseEntity<ResponseMessage> removeOrderItem(Long orderItemId) throws NotFoundException, BadRequestException;

    ResponseEntity<ResponseMessage> setAsComplete(Long orderId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsCompleteItem(Long orderItemId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsReturnItem(Long orderItemId) throws NotFoundException;

    // PROCUREMENT MANAGER ACCESS
    List<GetUserDTO> getSuppliers() throws NotFoundException;

    ResponseEntity<ResponseMessage> assignSupplier(Long orderId, Long supplierId) throws NotFoundException, BadRequestException;

    ResponseEntity<ResponseMessage> setAsApproved(Long orderId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsCanceled(Long orderId) throws NotFoundException;

    // SUPPLIER ACCESS
    ResponseEntity<ResponseMessage> setAsDelivered(Long orderId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsDeliveredItem(Long orderItemId) throws NotFoundException;

    ResponseEntity<ResponseMessage> setAsCancelledItem(Long orderItemId) throws NotFoundException;

    SiteDTO getSiteInfo(Long siteId) throws NotFoundException;
}
