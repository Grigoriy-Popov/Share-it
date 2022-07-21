package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final UserRepository userRepository;
    private Long id;
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getAllUsersItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item addItem(ItemDto itemDto, Long userId) {
        itemDto.setId(++id);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId))));
        item.setRequest(null);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long  itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item updateItem(ItemDto itemDto, Long itemId) {
        Item item = items.get(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setIsAvailable(itemDto.getAvailable());
        }
        return item;
    }

    @Override
    public List<Item> searchAvailableItems(String text) {
        return items.values().stream()
                .filter(Item::getIsAvailable)
                .filter(item -> item.getDescription().contains(text.toLowerCase()) ||
                        item.getName().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
