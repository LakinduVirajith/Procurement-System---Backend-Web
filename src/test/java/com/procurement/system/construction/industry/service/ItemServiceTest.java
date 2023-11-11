package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.ItemDTO;
import com.procurement.system.construction.industry.entity.Item;
import com.procurement.system.construction.industry.entity.OrderItem;
import com.procurement.system.construction.industry.exception.ConflictException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.repository.ItemRepository;
import com.procurement.system.construction.industry.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemServiceImpl itemService;
    private ItemRepository itemRepository;
    private OrderItemRepository orderItemRepository;
    private CommonFunctions commonFunctions;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        commonFunctions = mock(CommonFunctions.class);
        modelMapper = mock(ModelMapper.class);
        itemService = new ItemServiceImpl(itemRepository, orderItemRepository, commonFunctions, modelMapper);
    }

    @Test
    public void addItem_shouldReturnSuccessResponse_whenItemAddedSuccessfully() throws ConflictException {
        // Given
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName("Test Item");
        itemDTO.setDescription("Test Description");

        when(itemRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(modelMapper.map(any(ItemDTO.class), eq(Item.class))).thenReturn(new Item());

        // When
        ResponseEntity<ResponseMessage> responseEntity = itemService.addItem(itemDTO);

        // Then
        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.ok().build(), responseEntity);
    }

    @Test
    public void addItem_shouldThrowConflictException_whenItemAlreadyExists() throws ConflictException {
        // Given
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName("Test Item");
        itemDTO.setDescription("Test Description");

        when(itemRepository.findByName(anyString())).thenReturn(Optional.of(new Item()));

        // When and Then
        assertThrows(ConflictException.class, () -> itemService.addItem(itemDTO));
    }

    @Test
    public void getAllItems_shouldReturnItemDTOList_whenItemsExist() throws NotFoundException {
        // Given
        List<Item> itemList = Collections.singletonList(new Item());
        when(itemRepository.findAll()).thenReturn(itemList);
        when(modelMapper.map(any(Item.class), eq(ItemDTO.class))).thenReturn(new ItemDTO());
        // When
        List<ItemDTO> itemDTOList = itemService.getAllItems();

        // Then
        assertNotNull(itemDTOList);
        assertEquals(itemList.size(), itemDTOList.size());
    }

    @Test
    public void getAllItems_shouldThrowNotFoundException_whenNoItemsExist() throws NotFoundException {
        // Given
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        // When and Then
        assertThrows(NotFoundException.class, () -> itemService.getAllItems());
    }

    @Test
    public void getItem_shouldReturnItemDTO_whenItemExists() throws NotFoundException {
        // Given
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(modelMapper.map(any(Item.class), eq(ItemDTO.class))).thenReturn(new ItemDTO());

        // When
        ItemDTO itemDTO = itemService.getItem(itemId);

        // Then
        assertNotNull(itemDTO);
    }

    @Test
    public void getItem_shouldThrowNotFoundException_whenItemDoesNotExist() throws NotFoundException {
        // Given
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> itemService.getItem(itemId));
    }

    @Test
    public void updateItem_shouldReturnSuccessResponse_whenItemUpdatedSuccessfully() throws NotFoundException {
        // Given
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setItemId(1L);

        when(itemRepository.findById(itemDTO.getItemId())).thenReturn(Optional.of(new Item()));
        when(modelMapper.map(any(ItemDTO.class), eq(Item.class))).thenReturn(new Item());

        // When
        ResponseEntity<ResponseMessage> responseEntity = itemService.updateItem(itemDTO);

        // Then
        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.ok().build(), responseEntity);
    }

    @Test
    public void updateItem_shouldThrowNotFoundException_whenItemDoesNotExist() throws NotFoundException {
        // Given
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setItemId(1L);

        when(itemRepository.findById(itemDTO.getItemId())).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDTO));
    }

    @Test
    public void deleteItem_shouldReturnSuccessResponse_whenItemDeletedSuccessfully() throws NotFoundException, ConflictException {
        // Given
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(orderItemRepository.findFirstByOrderItemId(itemId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<ResponseMessage> responseEntity = itemService.deleteItem(itemId);

        // Then
        assertNotNull(responseEntity);
        assertEquals(ResponseEntity.ok().build(), responseEntity);
    }

    @Test
    public void deleteItem_shouldThrowNotFoundException_whenItemDoesNotExist() throws NotFoundException, ConflictException {
        // Given
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(NotFoundException.class, () -> itemService.deleteItem(itemId));
    }

    @Test
    public void deleteItem_shouldThrowConflictException_whenItemIsInUseInOrders() throws NotFoundException, ConflictException {
        // Given
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(orderItemRepository.findFirstByOrderItemId(itemId)).thenReturn(Optional.of(new OrderItem()));

        // When and Then
        assertThrows(ConflictException.class, () -> itemService.deleteItem(itemId));
    }
}