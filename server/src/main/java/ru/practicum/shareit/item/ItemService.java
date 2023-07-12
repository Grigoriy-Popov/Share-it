package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comments.Comment;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItemByIdByUser(Long itemId, Long userId);

    List<ItemDto> getAllUserItems(Long userId, int from, int size);

    ItemDto editItem(ItemDto itemDtoToUpdate, Long itemId, Long userId);

    List<ItemDto> searchAvailableItemsByKeyword(String text, int from, int size);

    Comment addComment(Comment comment, Long userId, Long itemId);

}
