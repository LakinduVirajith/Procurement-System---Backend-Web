package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.OrderDTO;
import com.procurement.system.construction.industry.dto.OrderItemDTO;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.entity.*;
import com.procurement.system.construction.industry.enums.Status;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final CommonFunctions commonFunctions;
    private final ModelMapper modelMapper;

    @Override
    public List<OrderDTO> getAllOrderDetails() throws NotFoundException {
        User user = commonFunctions.getUser();
        Long siteId = user.getSite().getSiteId();
        if(siteId == null){
            throw new NotFoundException("You are not currently assigned to any site.");
        }

        List<OrderDetails> orders = orderRepository.findBySiteSiteId(siteId);
        if(orders.isEmpty()){
            throw new NotFoundException("Haven't found any orders for this site yet.");
        }

        // SET ORDER DETAILS
        List<OrderDTO> orderDTOS = orders.stream().map(order -> {

            OrderDTO orderDTO = new OrderDTO();
            switch (user.getRole().name()) {
                case "PROCUREMENT_MANAGER" -> {
                    if (order.getStatus().name().equals("Pending") || order.getStatus().name().equals("Cancel")) {
                        orderDTO = modelMapper.map(order, OrderDTO.class);
                        orderDTO.setSupplierId(order.getSupplier().getUserId());
                        orderDTO.setSiteId(order.getSite().getSiteId());
                    }
                }
                case "SUPPLIER" -> {
                    if (order.getStatus().name().equals("Approval") || order.getStatus().name().equals("Return")) {
                        orderDTO = modelMapper.map(order, OrderDTO.class);
                        orderDTO.setSupplierId(order.getSupplier().getUserId());
                        orderDTO.setSiteId(order.getSite().getSiteId());
                    }
                }
                default -> orderDTO = modelMapper.map(order, OrderDTO.class);
            }

            // SET ORDER ITEMS
            if(!orderDTO.getItems().isEmpty()){
                GetOrderItemsData(order, orderDTO);
            }
            return orderDTO;
        }).collect(Collectors.toList());

        return orderDTOS;
    }

    @Override
    public OrderDTO getOrderDetails(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Couldn't find any orders with the given ID"));

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        GetOrderItemsData(order, orderDTO);
        return orderDTO;
    }

    private void GetOrderItemsData(OrderDetails order, OrderDTO orderDTO) {
        orderDTO.setSupplierId(order.getSupplier().getUserId());
        orderDTO.setSiteId(order.getSite().getSiteId());

        List<OrderItemDTO> orderItemDTOS = order.getItems()
                .stream()
                .map(item -> {
                    OrderItemDTO orderItemDTO = modelMapper.map(item, OrderItemDTO.class);
                    orderItemDTO.setOrderId(item.getOrder().getOrderId());
                    orderItemDTO.setItemId(item.getItem().getItemId());

                    return orderItemDTO;
                }).collect(Collectors.toList());

        orderDTO.setItems(orderItemDTOS);
    }

    @Override
    public List<OrderItemDTO> getOrderItems(Long orderId) throws NotFoundException {
        OrderDTO orderDTO = getOrderDetails(orderId);
        return orderDTO.getItems();
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> addOrder(OrderDTO orderDTO) throws NotFoundException {
        OrderDetails order = modelMapper.map(orderDTO, OrderDetails.class);
        order.setOrderId(null);
        order.setStatus(Status.Pending);
        order.setSupplier(null);

        Site site = commonFunctions.getUser().getSite();
        if(site == null){
            throw new NotFoundException("You are not currently assigned to any site.");
        }
        order.setSite(site);
        OrderDetails executedOrder = orderRepository.save(order);

        // SET ORDER ITEMS
        List<OrderItemDTO> orderItemDTOS = orderDTO.getItems();
        for (OrderItemDTO orderItemDTO : orderItemDTOS) {
            OrderItem orderItem = modelMapper.map(orderItemDTO, OrderItem.class);
            orderItem.setOrderItemId(null);

            Optional<Item> itemOptional = itemRepository.findById(orderItemDTO.getItemId());
            if (itemOptional.isPresent()) {
                orderItem.setItem(itemOptional.get());
                orderItem.setOrder(executedOrder);
                orderItemRepository.save(orderItem);
            }
        }

        return commonFunctions.successResponse("Order has been added successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> addOrderItem(OrderItemDTO orderItemDTO) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderItemDTO.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        Item item = itemRepository.findById(orderItemDTO.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with the provided ID"));

        OrderItem orderItem = modelMapper.map(orderItemDTO, OrderItem.class);
        orderItem.setOrderItemId(null);
        orderItem.setItem(item);
        orderItem.setOrder(order);

        orderItemRepository.save(orderItem);
        return commonFunctions.successResponse("Order Item has been added successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> removeOrderItem(Long orderItemId) throws NotFoundException {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with the provided ID"));

        orderItem.setOrder(null);
        orderItem.setItem(null);
        orderItemRepository.save(orderItem);
        orderItemRepository.deleteById(orderItemId);
        return commonFunctions.successResponse("Order Item has been removed successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsComplete(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        order.setStatus(Status.Complete);
        orderRepository.save(order);
        return  commonFunctions.successResponse("Order Status has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsCompleteItem(Long orderItemId) throws NotFoundException {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with the provided ID"));

        orderItem.setStatus(Status.Complete);
        orderItemRepository.save(orderItem);
        return  commonFunctions.successResponse("Order Item Status has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsReturnItem(Long orderItemId) throws NotFoundException {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with the provided ID"));

        orderItem.setStatus(Status.Return);
        orderItemRepository.save(orderItem);
        return commonFunctions.successResponse("Order Item Status has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> assignSupplier(Long orderId, Long supplierId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        User user = userRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Supplier not found with the provided ID"));

        order.setSupplier(user);
        orderRepository.save(order);
        return commonFunctions.successResponse("Supplier has been assigned successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsApproved(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        order.setStatus(Status.Approval);
        orderRepository.save(order);
        return  commonFunctions.successResponse("Order Status has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsCanceled(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        order.setStatus(Status.Cancel);
        orderRepository.save(order);
        return  commonFunctions.successResponse("Order Status has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsDelivered(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        order.setStatus(Status.Delivered);
        orderRepository.save(order);
        return  commonFunctions.successResponse("Order Status has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsDeliveredItem(Long orderItemId) throws NotFoundException {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with the provided ID"));

        orderItem.setStatus(Status.Delivered);
        orderItemRepository.save(orderItem);
        return  commonFunctions.successResponse("Order Item Status has been updated successfully");
    }

    @Override
    public SiteDTO getSiteInfo(Long siteId) throws NotFoundException {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site not found with the provided ID"));

        SiteDTO siteDTO = modelMapper.map(site, SiteDTO.class);
        siteDTO.setSiteId(null);
        siteDTO.setAllocatedBudget(0);
        siteDTO.setSiteManagerId(null);
        siteDTO.setProcurementManagerId(null);

        return siteDTO;
    }
}
