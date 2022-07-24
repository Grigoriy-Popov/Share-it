package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository {

    Item addItem(ItemDto itemDto, Long userId);

    Optional<Item> getItemById(Long itemId);

    List<Item> getAllUsersItems(Long userId);

    Item editItem(ItemDto itemDto, Long userId);

    List<Item> searchAvailableItems(String text);
}
