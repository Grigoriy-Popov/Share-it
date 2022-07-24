package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        return toItemDto(itemRepository.addItem(itemDto, userId));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return toItemDto(itemRepository.getItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId))));
    }

    @Override
    public List<ItemDto> getAllUsersItems(Long userId) {
        return itemRepository.getAllUsersItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", itemId)));
        if (item.getOwner().getId().equals(userId)) {
            return toItemDto(itemRepository.editItem(itemDto, itemId));
        } else {
            throw new UserIsNotOwnerException(String.format("User with id %d is not the owner of the item", userId));
        }
    }

    public List<ItemDto> searchAvailableItems(String text) {
        List<Item> items = itemRepository.searchAvailableItems(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
