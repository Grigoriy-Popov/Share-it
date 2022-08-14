package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllUsersItems(Long userId);

    ItemDto editItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchAvailableItems(String text);

    Comment addComment(Comment comment, Long userId, Long itemId);
}
