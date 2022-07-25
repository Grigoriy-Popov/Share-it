package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository {

    Item addItem(ItemDto itemDto, User user);

    Optional<Item> getItemById(Long itemId);

    List<Item> getAllUsersItems(Long userId);

    Item editItem(ItemDto itemDto, Long userId);

    List<Item> searchAvailableItems(String text);
}
