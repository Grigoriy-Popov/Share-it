package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> getAllUsersItems(Long userId) {
        return itemRepository.getAllUsersItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        System.out.println(itemDto);
        return toItemDto(itemRepository.addItem(itemDto, userId));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return toItemDto(itemRepository.getItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId))));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId)));
        if (item.getOwner().getId().equals(userId)) {
            return toItemDto(itemRepository.updateItem(itemDto, itemId));
        } else {
            throw new NotOwnerException(String.format("User with id %d is not the owner of the item", userId));
        }
    }

    public List<ItemDto> searchAvailableItems(String text) {
        List<Item> items = itemRepository.searchAvailableItems(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
