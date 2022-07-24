package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllUsersItems(Long userId);

    ItemDto editItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchAvailableItems(String text);
}
