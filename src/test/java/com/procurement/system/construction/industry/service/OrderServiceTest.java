package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.OrderDetailsDTO;
import com.procurement.system.construction.industry.dto.OrderItemDTO;
import com.procurement.system.construction.industry.entity.*;
import com.procurement.system.construction.industry.enums.Status;
import com.procurement.system.construction.industry.exception.BadRequestException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.repository.*;
import jakarta.persistence.EntityManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private CommonFunctions commonFunctions;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
    }

    @Test
    @SneakyThrows
    public void getAllOrderDetails_shouldReturnOrderDetailsList_whenOrdersExist() {
        // Given
        User user = new User();
        Site site = new Site();
        site.setSiteId(1L);
        user.setSite(site);
        Mockito.when(commonFunctions.getUser()).thenReturn(user);

        OrderDetails order = new OrderDetails();
        order.setOrderId(1L);
        order.setSite(site);
        Mockito.when(orderRepository.findBySiteSiteId(1L)).thenReturn(Collections.singletonList(order));

        // When
        List<OrderDetailsDTO> orderDetailsList = orderService.getAllOrderDetails();

        // Then
        assertNotNull(orderDetailsList);
        assertFalse(orderDetailsList.isEmpty());
    }

    @Test
    @SneakyThrows
    public void getAllOrderDetails_shouldThrowNotFoundException_whenUserIsNotAssignedToSite() {
        // Given
        User user = new User();
        Mockito.when(commonFunctions.getUser()).thenReturn(user);

        // When and Then
        assertThrows(NotFoundException.class, () -> orderService.getAllOrderDetails());
    }

    @Test
    public void getAllOrderDetails_shouldThrowNotFoundException_whenNoOrdersFound() throws NotFoundException {
        // Given
        User user = new User();
        Site site = new Site();
        site.setSiteId(1L);
        user.setSite(site);
        Mockito.when(commonFunctions.getUser()).thenReturn(user);

        Mockito.when(orderRepository.findBySiteSiteId(1L)).thenReturn(Collections.emptyList());

        // When and Then
        assertThrows(NotFoundException.class, () -> orderService.getAllOrderDetails());
    }

    @Test
    public void addOrder_shouldReturnSuccessResponse_whenOrderAddedSuccessfully() throws NotFoundException {
        // Given
        OrderDetailsDTO orderDTO = new OrderDetailsDTO();
        OrderDetails order = new OrderDetails();

        User user = new User();
        Site site = new Site();
        site.setSiteId(1L);
        user.setSite(site);
        Mockito.when(commonFunctions.getUser()).thenReturn(user);

        Mockito.when(modelMapper.map(orderDTO, OrderDetails.class)).thenReturn(order);
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        // When
        ResponseEntity<ResponseMessage> responseEntity = orderService.addOrder(orderDTO);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("order has been added successfully", responseEntity.getBody().getMessage());
    }

    @Test
    @SneakyThrows
    public void addOrder_shouldThrowNotFoundException_whenUserIsNotAssignedToSite() {
        // Given
        OrderDetailsDTO orderDTO = new OrderDetailsDTO();
        User user = new User();
        Mockito.when(commonFunctions.getUser()).thenReturn(user);

        // When and Then
        assertThrows(NotFoundException.class, () -> orderService.addOrder(orderDTO));
    }

    @Test
    public void addOrderItem_shouldReturnSuccessResponse_whenOrderItemAddedSuccessfully() throws NotFoundException {
        // Given
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        OrderDetails order = new OrderDetails();
        Item item = new Item();

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        Mockito.when(entityManager.merge(item)).thenReturn(item);

        OrderItem orderItem = new OrderItem();
        Mockito.when(modelMapper.map(orderItemDTO, OrderItem.class)).thenReturn(orderItem);
        Mockito.when(orderItemRepository.save(orderItem)).thenReturn(orderItem);

        // When
        ResponseEntity<ResponseMessage> responseEntity = orderService.addOrderItem(orderItemDTO);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Order Item has been added successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void addOrderItem_shouldThrowNotFoundException_whenOrderDoesNotExist() {
        // Given
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> orderService.addOrderItem(orderItemDTO));
    }

    @Test
    public void addOrderItem_shouldThrowNotFoundException_whenItemDoesNotExist() {
        // Given
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(new OrderDetails()));
        Mockito.when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> orderService.addOrderItem(orderItemDTO));
    }

    @Test
    @SneakyThrows
    public void removeOrderItem_shouldReturnSuccessResponse_whenOrderItemRemovedSuccessfully() throws NotFoundException {
        // Given
        OrderItem orderItem = new OrderItem();
        OrderDetails order = new OrderDetails();
        order.setOrderId(1L);
        order.setItems(Collections.singletonList(orderItem));

        Mockito.when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.doReturn(orderItem).when(orderItemRepository).save(Mockito.any(OrderItem.class));

        // When
        ResponseEntity<ResponseMessage> responseEntity = orderService.removeOrderItem(1L);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Order Item has been removed successfully", responseEntity.getBody().getMessage());
    }

    @Test
    public void removeOrderItem_shouldThrowNotFoundException_whenOrderItemDoesNotExist() {
        // Given
        Mockito.when(orderItemRepository.findById(1L)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> orderService.removeOrderItem(1L));
    }

    @Test
    public void removeOrderItem_shouldThrowBadRequestException_whenOrderHasSingleItem() {
        // Given
        OrderItem orderItem = new OrderItem();
        OrderDetails order = new OrderDetails();
        order.setOrderId(1L);
        order.setItems(Collections.singletonList(orderItem));

        Mockito.when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When and Then
        assertThrows(BadRequestException.class, () -> orderService.removeOrderItem(1L));
    }

    @Test
    public void setAsComplete_shouldReturnSuccessResponse_whenOrderSetAsComplete() throws NotFoundException {
        // Given
        OrderDetails order = new OrderDetails();
        OrderItem orderItem = new OrderItem();
        orderItem.setStatus(Status.Pending);
        order.setItems(Collections.singletonList(orderItem));

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Mockito.doReturn(orderItem).when(orderItemRepository).save(Mockito.any(OrderItem.class));
        Mockito.when(orderRepository.save(order)).thenReturn(order);

        // When
        ResponseEntity<ResponseMessage> responseEntity = orderService.setAsComplete(1L);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Order Status has been updated successfully", responseEntity.getBody().getMessage());
    }

    @Test
    @SneakyThrows
    public void setAsCompleteItem_shouldReturnSuccessResponse_whenOrderItemSetAsComplete() {
        // Given
        OrderItem orderItem = new OrderItem();
        orderItem.setStatus(Status.Pending);

        Mockito.when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        Mockito.when(orderItemRepository.save(orderItem)).thenReturn(orderItem);

        // When
        ResponseEntity<ResponseMessage> responseEntity = orderService.setAsCompleteItem(1L);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Order Item Status has been updated successfully", responseEntity.getBody().getMessage());
    }
}