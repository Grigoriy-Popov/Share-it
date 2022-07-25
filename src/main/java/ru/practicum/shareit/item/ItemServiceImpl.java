package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User user = userService.getUserById(userId); // Для проверки существует ли владелец вещи
        return toItemDto(itemRepository.addItem(itemDto, user));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return toItemDto(itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d not found", itemId))));
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
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d not found", itemId)));
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
