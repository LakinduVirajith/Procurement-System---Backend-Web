package com.procurement.system.construction.industry.controller;

import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.OrderDTO;
import com.procurement.system.construction.industry.dto.OrderItemDTO;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@Tag(name = "Order Controllers")
public class OrderController {

    private final OrderService orderService;

    // ALL USERS ACCESS
    @GetMapping("all-users/order/all/details")
    public List<OrderDTO> getAllOrderDetails() throws NotFoundException {
        return orderService.getAllOrderDetails();
    }

    @GetMapping("all-users/order/details/{id}")
    public OrderDTO getOrderDetails(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.getOrderDetails(orderId);
    }

    @GetMapping("all-users/order/details/items/{id}")
    public List<OrderItemDTO> getOrderItems(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.getOrderItems(orderId);
    }

    // SITE MANAGER ACCESS
    @PostMapping("site-manager/order/add")
    public ResponseEntity<ResponseMessage> addOrder(@Valid @RequestBody OrderDTO orderDTO) throws NotFoundException {
        return orderService.addOrder(orderDTO);
    }

    @PutMapping("site-manager/order/add/item")
    public ResponseEntity<ResponseMessage> addOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO) throws NotFoundException {
        return orderService.addOrderItem(orderItemDTO);
    }

    @DeleteMapping("site-manager/order/delete/item/{id}")
    public ResponseEntity<ResponseMessage> removeOrderItem(@PathVariable("id") Long orderItemId) throws NotFoundException {
        return orderService.removeOrderItem(orderItemId);
    }

    @PutMapping("site-manager/order/complete/{id}")
    public ResponseEntity<ResponseMessage> setAsComplete(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.setAsComplete(orderId);
    }

    @PutMapping("site-manager/item/complete/{id}")
    public ResponseEntity<ResponseMessage> setAsCompleteItem(@PathVariable("id") Long orderItemId) throws NotFoundException {
        return orderService.setAsCompleteItem(orderItemId);
    }

    @PutMapping("site-manager/item/return/{id}")
    public ResponseEntity<ResponseMessage> setAsReturnItem(@PathVariable("id") Long orderItemId) throws NotFoundException {
        return orderService.setAsReturnItem(orderItemId);
    }

    // PROCUREMENT MANAGER ACCESS
    @PutMapping("procurement-manager/order/assign/{id}")
    public ResponseEntity<ResponseMessage> assignSupplier(@PathVariable("id") Long orderId, @RequestBody Long supplierId) throws NotFoundException {
        return orderService.assignSupplier(orderId, supplierId);
    }
    @PutMapping("procurement-manager/order/approval/{id}")
    public ResponseEntity<ResponseMessage> setAsApproved(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.setAsApproved(orderId);
    }

    @PutMapping("procurement-manager/order/cancel/{id}")
    public ResponseEntity<ResponseMessage> setAsCanceled(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.setAsCanceled(orderId);
    }

    // SUPPLIER ACCESS
    @PutMapping("supplier/order/delivered/{id}")
    public ResponseEntity<ResponseMessage> setAsDelivered(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.setAsDelivered(orderId);
    }

    @PutMapping("supplier/item/delivered/{id}")
    public ResponseEntity<ResponseMessage> setAsDeliveredItem(@PathVariable("id") Long orderItemId) throws NotFoundException {
        return orderService.setAsDeliveredItem(orderItemId);
    }

    @GetMapping("supplier/get/site/info/{id}")
    public SiteDTO getSiteInfo(@PathVariable("id") Long siteId) throws NotFoundException {
        return orderService.getSiteInfo(siteId);
    }
}
