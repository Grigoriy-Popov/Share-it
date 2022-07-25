package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    private Long id = 0L;
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(ItemDto itemDto, User user) {
        itemDto.setId(++id);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long  itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAllUsersItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item editItem(ItemDto itemDto, Long itemId) {
        Item item = items.get(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    @Override
    public List<Item> searchAvailableItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        item.getName().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
