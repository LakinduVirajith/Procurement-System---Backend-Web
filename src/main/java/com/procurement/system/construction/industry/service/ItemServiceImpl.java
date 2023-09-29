package com.procurement.system.construction.industry.service;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.ItemDTO;
import com.procurement.system.construction.industry.entity.Item;
import com.procurement.system.construction.industry.exception.ConflictException;
import com.procurement.system.construction.industry.exception.NotFoundException;
import com.procurement.system.construction.industry.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{

    private final ItemRepository itemRepository;
    private final CommonFunctions commonFunctions;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseMessage> addItem(ItemDTO itemDTO) throws ConflictException {
        Optional<Item> optionalItem = itemRepository.findByName(itemDTO.getName());
        if(optionalItem.isPresent()){
            throw new ConflictException("The item already exists");
        }

        itemDTO.setItemId(null);
        Item item = new Item();
        modelMapper.map(itemDTO, item);

        itemRepository.save(item);
        return commonFunctions.successResponse("The item has been listed successfully");
    }

    @Override
    public List<ItemDTO> getAllItems() throws NotFoundException {
        List<Item> itemList = itemRepository.findAll();
        if(itemList.isEmpty()){
            throw new NotFoundException("Couldn't find any items in system");
        }

        List<ItemDTO> itemDTOS = itemList.stream()
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toList());

        return itemDTOS;
    }

    @Override
    public ItemDTO getItem(Long itemId) throws NotFoundException {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if(optionalItem.isEmpty()){
            throw new NotFoundException("Couldn't find any item with the provided ID");
        }

        ItemDTO itemDTO = new ItemDTO();
        modelMapper.map(optionalItem.get(), itemDTO);
        return itemDTO;
    }

    @Override
    public ResponseEntity<ResponseMessage> updateItem(ItemDTO itemDTO) throws NotFoundException {
        Optional<Item> optionalItem = itemRepository.findById(itemDTO.getItemId());
        if(optionalItem.isEmpty()){
            throw new NotFoundException("Couldn't find any item with the provided ID");
        }

        Item item = optionalItem.get();
        modelMapper.map(itemDTO, item);
        itemRepository.save(item);

        return commonFunctions.successResponse("The item has been updated successfully");
    }

    @Override
    public ResponseEntity<ResponseMessage> deleteItem(Long itemId) throws NotFoundException {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if(optionalItem.isEmpty()){
            throw new NotFoundException("Couldn't find any item with the provided ID");
        }

        itemRepository.deleteById(itemId);
        return commonFunctions.successResponse("The item has been deleted successfully");
    }
}
