package com.procurement.system.construction.industry.controller;

import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.GetUserDTO;
import com.procurement.system.construction.industry.dto.OrderDetailsDTO;
import com.procurement.system.construction.industry.dto.OrderItemDTO;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Get All Order Info", description = "Retrieve details of all orders.")
    @GetMapping("all-users/order/all/details")
    public List<OrderDetailsDTO> getAllOrderDetails() throws NotFoundException {
        return orderService.getAllOrderDetails();
    }

    // SITE MANAGER ACCESS
    @Operation(summary = "Add a New Order", description = " Add a new order providing necessary details.")
    @PostMapping("site-manager/order/add")
    public ResponseEntity<ResponseMessage> addOrder(@Valid @RequestBody OrderDetailsDTO orderDTO) throws NotFoundException {
        return orderService.addOrder(orderDTO);
    }

    @Operation(summary = "Add an Item to an Order.", description = "Add an item to an order. providing necessary details.")
    @PutMapping("site-manager/order/add/item")
    public ResponseEntity<ResponseMessage> addOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO) throws NotFoundException {
        return orderService.addOrderItem(orderItemDTO);
    }

    @Operation(summary = "Add an Item from Order", description = "Remove an item from an order using orderItemId.")
    @DeleteMapping("site-manager/order/delete/item/{id}")
    public ResponseEntity<ResponseMessage> removeOrderItem(@PathVariable("id") Long orderItemId) throws NotFoundException, BadRequestException {
        return orderService.removeOrderItem(orderItemId);
    }

    @Operation(summary = "Mark Order as Complete", description = "Use this option to indicate that an order has been successfully completed.")
    @PutMapping("site-manager/order/complete/{id}")
    public ResponseEntity<ResponseMessage> setAsComplete(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.setAsComplete(orderId);
    }

    @Operation(summary = "Mark Item as Completed", description = "Use this option to indicate that an item within the order has been successfully completed.")
    @PutMapping("site-manager/item/complete/{id}")
    public ResponseEntity<ResponseMessage> setAsCompleteItem(@PathVariable("id") Long orderItemId) throws NotFoundException {
        return orderService.setAsCompleteItem(orderItemId);
    }

    @Operation(summary = "Mark Item for Return", description = "Use this option to request a return for an item within the order.")
    @PutMapping("site-manager/item/return/{id}")
    public ResponseEntity<ResponseMessage> setAsReturnItem(@PathVariable("id") Long orderItemId) throws NotFoundException {
        return orderService.setAsReturnItem(orderItemId);
    }

    // PROCUREMENT MANAGER ACCESS
    @Operation(summary = "Get Supplier", description = "Get supplier details on a particular site.")
    @GetMapping("procurement-manager/get/supplier")
    public List<GetUserDTO> getSuppliers() throws NotFoundException {
        return orderService.getSuppliers();
    }

    @Operation(summary = "Assign Supplier to Order", description = "Assign a supplier to fulfill this order by specifying the supplier's ID.")
    @PutMapping("procurement-manager/order/assign/{id}")
    public ResponseEntity<ResponseMessage> assignSupplier(@PathVariable("id") Long orderId, @RequestBody Long supplierId) throws NotFoundException, BadRequestException {
        return orderService.assignSupplier(orderId, supplierId);
    }

    @Operation(summary = "Approve Order", description = "Approve this order for procurement and further processing.")
    @PutMapping("procurement-manager/order/approval/{id}")
    public ResponseEntity<ResponseMessage> setAsApproved(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.setAsApproved(orderId);
    }

    @Operation(summary = "Cancel Order", description = "Cancel this order, preventing further processing.")
    @PutMapping("procurement-manager/order/cancel/{id}")
    public ResponseEntity<ResponseMessage> setAsCanceled(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.setAsCanceled(orderId);
    }

    // SUPPLIER ACCESS
    @Operation(summary = "Mark Order as Delivered", description = "Use this option to confirm that the entire order has been successfully delivered.")
    @PutMapping("supplier/order/delivered/{id}")
    public ResponseEntity<ResponseMessage> setAsDelivered(@PathVariable("id") Long orderId) throws NotFoundException {
        return orderService.setAsDelivered(orderId);
    }

    @Operation(summary = "Mark Item as Delivered", description = "Use this option to confirm the delivery of a specific item within the order.")
    @PutMapping("supplier/item/delivered/{id}")
    public ResponseEntity<ResponseMessage> setAsDeliveredItem(@PathVariable("id") Long orderItemId) throws NotFoundException {
        return orderService.setAsDeliveredItem(orderItemId);
    }

    @Operation(summary = "Mark Item as Cancelled", description = "Use this option to cancel the delivery of a specific item within the order.")
    @PutMapping("supplier/item/cancelled/{id}")
    public ResponseEntity<ResponseMessage> setAsCancelledItem(@PathVariable("id") Long orderItemId) throws NotFoundException {
        return orderService.setAsCancelledItem(orderItemId);
    }

    @Operation(summary = "Retrieve Site Information", description = "Retrieve information about the site associated with this order.")
    @GetMapping("supplier/get/site/info/{id}")
    public SiteDTO getSiteInfo(@PathVariable("id") Long siteId) throws NotFoundException {
        return orderService.getSiteInfo(siteId);
    }
}
