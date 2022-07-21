package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Component
public interface ItemRepository {

    List<Item> getAllUsersItems(Long userId);

    Item addItem(ItemDto itemDto, Long userId);

    Item updateItem(ItemDto itemDto, Long userId);

    Optional<Item> getItemById(Long itemId);

    List<Item> searchAvailableItems(String text);
}
