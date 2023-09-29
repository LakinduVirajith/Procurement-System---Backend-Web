package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.ItemDTO;
import com.procurement.system.construction.industry.exception.ConflictException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ItemService {
    ResponseEntity<ResponseMessage> addItem(ItemDTO itemDTO) throws ConflictException;

    List<ItemDTO> getAllItems() throws NotFoundException;

    ItemDTO getItem(Long itemId) throws NotFoundException;

    ResponseEntity<ResponseMessage> updateItem(ItemDTO itemDTO) throws NotFoundException;

    ResponseEntity<ResponseMessage> deleteItem(Long itemId) throws NotFoundException;
}
