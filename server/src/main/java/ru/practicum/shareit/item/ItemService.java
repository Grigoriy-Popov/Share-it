package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comments.Comment;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllUserItems(Long userId, Integer from, Integer size);

    ItemDto editItem(ItemDto itemDto, Long itemId, Long userId);

    List<ItemDto> searchAvailableItemsByKeyword(String text, Integer from, Integer size);

    Comment addComment(Comment comment, Long userId, Long itemId);
}
