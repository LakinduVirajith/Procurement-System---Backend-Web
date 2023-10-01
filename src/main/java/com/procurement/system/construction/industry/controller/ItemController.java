package com.procurement.system.construction.industry.controller;


import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.ItemDTO;
import com.procurement.system.construction.industry.exception.ConflictException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/site-manager/")
@RequiredArgsConstructor
@Tag(name = "Item Controllers")
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "Add Item", description = "Add a new item to the system.")
    @PostMapping("item/add")
    public ResponseEntity<ResponseMessage> addItem(@Valid @RequestBody ItemDTO itemDTO) throws ConflictException {
        return itemService.addItem(itemDTO);
    }

    @Operation(summary = "Get All Items", description = "Retrieve details of all items in the system.")
    @GetMapping("item/get/all")
    public List<ItemDTO> getAllItems() throws NotFoundException {
        return itemService.getAllItems();
    }

    @Operation(summary = "Get Item Details", description = "Retrieve details of a specific item by its ID.")
    @GetMapping("item/get/{id}")
    public ItemDTO getItem(@PathVariable("id") Long itemId) throws NotFoundException {
        return itemService.getItem(itemId);
    }

    @Operation(summary = "Update Item", description = "Update the details of an existing item.")
    @PutMapping("item/update")
    public ResponseEntity<ResponseMessage> updateItem(@Valid @RequestBody ItemDTO itemDTO) throws NotFoundException {
        return itemService.updateItem(itemDTO);
    }

    @Operation(summary = "Delete Item", description = "Delete a specific item by its ID.")
    @DeleteMapping("item/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteItem(@PathVariable("id") Long itemId) throws NotFoundException, ConflictException {
        return itemService.deleteItem(itemId);
    }
}
