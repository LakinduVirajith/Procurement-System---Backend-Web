package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.GetUserDTO;
import com.procurement.system.construction.industry.dto.OrderDetailsDTO;
import com.procurement.system.construction.industry.dto.OrderItemDTO;
import com.procurement.system.construction.industry.dto.SiteDTO;
import com.procurement.system.construction.industry.entity.*;
import com.procurement.system.construction.industry.enums.Status;
import com.procurement.system.construction.industry.enums.UserRole;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final EntityManager entityManager;

    @Override
    @Transactional
    public List<OrderDetailsDTO> getAllOrderDetails() throws NotFoundException {
        User user = commonFunctions.getUser();
        if (user.getSite() == null) {
            throw new NotFoundException("you are not currently assigned to any site.");
        }

        Long siteId = user.getSite().getSiteId();
        List<OrderDetails> orders = orderRepository.findBySiteSiteId(siteId);
        if (orders.isEmpty()) {
            throw new NotFoundException("haven't found any orders for this site yet.");
        }

        return orders.stream()
                .map(order -> mapOrderToDTO(order, user.getRole()))
                .collect(Collectors.toList());
    }

    private OrderDetailsDTO mapOrderToDTO(OrderDetails order, UserRole userRole) {
        OrderDetailsDTO detailsDTO = OrderDetailsDTO.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .requiredDate(order.getRequiredDate())
                .siteId(order.getSite().getSiteId()).build();

        if(order.getSupplier() != null){
            detailsDTO.setSupplierId(order.getSupplier().getUserId());
        }

        if (userRole == UserRole.PROCUREMENT_MANAGER) {
            if (isOrderStatusValidForProcurementManager(order.getStatus())) {
                detailsDTO.setItems(GetOrderItemsData(order));
            }
        } else if (userRole == UserRole.SUPPLIER) {
            if (isOrderStatusValidForSupplier(order.getStatus())) {
                detailsDTO.setItems(GetOrderItemsData(order));
            }
        } else {
            detailsDTO.setItems(GetOrderItemsData(order));
        }

        return detailsDTO;
    }

    private boolean isOrderStatusValidForProcurementManager(Status status) {
        return status == Status.Pending || status == Status.Approved || status == Status.Cancelled;
    }

    private boolean isOrderStatusValidForSupplier(Status status) {
        return status == Status.Approved || status == Status.Returned;
    }

    private List<OrderItemDTO> GetOrderItemsData(OrderDetails order) {
        return order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                            .orderItemId(item.getOrderItemId())
                            .quantity(item.getQuantity())
                            .status(item.getStatus())
                            .itemId(item.getItem().getItemId())
                            .orderId(order.getOrderId()).build()
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> addOrder(OrderDetailsDTO orderDTO) throws NotFoundException {
        OrderDetails order = modelMapper.map(orderDTO, OrderDetails.class);
        order.setOrderId(null);
        order.setStatus(Status.Pending);
        order.setSupplier(null);
        order.setItems(null);

        Site site = commonFunctions.getUser().getSite();
        if(site == null){
            throw new NotFoundException("you are not currently assigned to any site.");
        }
        order.setSite(site);
        OrderDetails executedOrder = orderRepository.save(order);

        // SET ORDER ITEMS
        List<OrderItemDTO> orderItemDTOS = orderDTO.getItems();
        for (OrderItemDTO orderItemDTO : orderItemDTOS) {
            OrderItem orderItem = modelMapper.map(orderItemDTO, OrderItem.class);
            orderItem.setOrderItemId(null);
            orderItem.setStatus(Status.Pending);

            Optional<Item> itemOptional = itemRepository.findById(orderItemDTO.getItemId());
            if (itemOptional.isPresent()) {
                orderItem.setItem(itemOptional.get());
                orderItem.setOrder(executedOrder);
                orderItemRepository.save(orderItem);
            }
        }

        return commonFunctions.successResponse("order has been added successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> addOrderItem(OrderItemDTO orderItemDTO) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderItemDTO.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        Item item = itemRepository.findById(orderItemDTO.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with the provided ID"));

        item = entityManager.merge(item);

        OrderItem orderItem = modelMapper.map(orderItemDTO, OrderItem.class);
        orderItem.setOrderItemId(null);
        orderItem.setStatus(Status.Pending);
        orderItem.setItem(item);
        orderItem.setOrder(order);

        orderItemRepository.save(orderItem);
        return commonFunctions.successResponse("Order Item has been added successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> removeOrderItem(Long orderItemId) throws NotFoundException, BadRequestException {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with the provided ID"));

        OrderDetails order = orderRepository.findById(orderItem.getOrder().getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        if(order.getItems().size() == 1){
            throw new BadRequestException("An order must contain at least one item");
        }

        orderItem.setOrder(null);
        orderItem.setItem(null);
        orderItemRepository.save(orderItem);
        orderItemRepository.deleteById(orderItemId);
        return commonFunctions.successResponse("Order Item has been removed successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> setAsComplete(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        // CREATE A COPY OF THE ORDER ITEMS LIST TO ITERATE OVER
        List<OrderItem> orderItemsCopy = new ArrayList<>(order.getItems());

        // SET THE STATUS OF EACH ORDER ITEM
        for (OrderItem item : orderItemsCopy) {
            item.setStatus(Status.Completed);
            orderItemRepository.save(item);
        }

        // UPDATE THE ORDER STATUS
        order.setStatus(Status.Completed);
        orderRepository.save(order);

        return  commonFunctions.successResponse("Order Status has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsCompleteItem(Long orderItemId) throws NotFoundException {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with the provided ID"));

        orderItem.setStatus(Status.Completed);
        orderItemRepository.save(orderItem);
        return  commonFunctions.successResponse("Order Item Status has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> setAsReturnItem(Long orderItemId) throws NotFoundException {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with the provided ID"));

        orderItem.setStatus(Status.Returned);
        orderItemRepository.save(orderItem);
        return commonFunctions.successResponse("Order Item Status has been updated successfully");
    }

    @Override
    public List<GetUserDTO> getSuppliers() throws NotFoundException {
        User user = commonFunctions.getUser();
        if (user.getSite() == null) {
            throw new NotFoundException("you are not currently assigned to any site");
        }

        Long siteId = user.getSite().getSiteId();
        List<User> users = userRepository.findByRoleAndSiteSiteIdAndIsActive(UserRole.SUPPLIER, siteId, true);
        if (users.isEmpty()) {
            throw new NotFoundException("no suppliers have been assigned to this site yet");
        }

        List<GetUserDTO> userDTOs = users.stream()
            .map(userEntity -> modelMapper.map(userEntity, GetUserDTO.class))
            .collect(Collectors.toList());

        return userDTOs;
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> assignSupplier(Long orderId, Long supplierId) throws NotFoundException, BadRequestException {
        User user = commonFunctions.getUser();
        if (user.getSite() == null) {
            throw new NotFoundException("you are not currently assigned to any site");
        }
        
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order looking for does not exist"));

        User supplier = userRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("supplier looking for does not exist"));

        if(!order.getSite().getSiteId().equals(user.getSite().getSiteId())){
            throw new BadRequestException("invalid order id");
        }
        if(!supplier.getRole().name().equals("SUPPLIER")){
            throw new BadRequestException("invalid supplier role");
        }
        if(!order.getSite().getSiteId().equals(user.getSite().getSiteId())){
            throw new BadRequestException("invalid supplier assignment for this order");
        }

        order.setSupplier(supplier);

        List<OrderDetails> supplierOrders = supplier.getOrders();
        supplierOrders.add(order);

        orderRepository.save(order);
        userRepository.save(supplier);
        return commonFunctions.successResponse("supplier has been assigned successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> setAsApproved(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        // CREATE A COPY OF THE ORDER ITEMS LIST TO ITERATE OVER
        List<OrderItem> orderItemsCopy = new ArrayList<>(order.getItems());

        // SET THE STATUS OF EACH ORDER ITEM
        for (OrderItem item : orderItemsCopy) {
            item.setStatus(Status.Approved);
            orderItemRepository.save(item);
        }

        // UPDATE THE ORDER STATUS
        order.setStatus(Status.Approved);
        orderRepository.save(order);

        return  commonFunctions.successResponse("Order Status has been updated successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> setAsCanceled(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        // CREATE A COPY OF THE ORDER ITEMS LIST TO ITERATE OVER
        List<OrderItem> orderItemsCopy = new ArrayList<>(order.getItems());

        // SET THE STATUS OF EACH ORDER ITEM
        for (OrderItem item : orderItemsCopy) {
            item.setStatus(Status.Cancelled);
            orderItemRepository.save(item);
        }

        // UPDATE THE ORDER STATUS
        order.setStatus(Status.Cancelled);
        orderRepository.save(order);

        return  commonFunctions.successResponse("Order Status has been updated successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseMessage> setAsDelivered(Long orderId) throws NotFoundException {
        OrderDetails order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with the provided ID"));

        // CREATE A COPY OF THE ORDER ITEMS LIST TO ITERATE OVER
        List<OrderItem> orderItemsCopy = new ArrayList<>(order.getItems());

        // SET THE STATUS OF EACH ORDER ITEM
        for (OrderItem item : orderItemsCopy) {
            item.setStatus(Status.Delivered);
            orderItemRepository.save(item);
        }

        // UPDATE THE ORDER STATUS
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
    public ResponseEntity<ResponseMessage> setAsCancelledItem(Long orderItemId) throws NotFoundException {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order Item not found with the provided ID"));

        orderItem.setStatus(Status.Cancelled);
        orderItemRepository.save(orderItem);
        return  commonFunctions.successResponse("Order Item Status has been updated successfully");
    }

    @Override
    public SiteDTO getSiteInfo(Long siteId) throws NotFoundException {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new NotFoundException("Site not found with the provided ID"));

        SiteDTO siteDTO = modelMapper.map(site, SiteDTO.class);
        siteDTO.setAllocatedBudget(0);
        siteDTO.setSiteManagerId(null);
        siteDTO.setProcurementManagerId(null);

        return siteDTO;
    }
}
