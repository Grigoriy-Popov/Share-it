package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserHasNotBookedItem;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        userService.getUserById(userId); // Для проверки существует ли владелец вещи
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d not found", itemId)));
        if (item.getOwnerId().equals(userId)) {
            setLastAndNextBooking(item);
        }
        ItemDto itemDto = ItemMapper.toDto(item);
        itemDto.setComments(CommentMapper.toDtoList(commentRepository.findAllByItemId(item.getId())));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllUsersItems(Long userId) {
        List<ItemDto> items = itemRepository.getAllByOwnerId(userId).stream()
                .map(this::setLastAndNextBooking)
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : items) {
            itemDto.setComments(CommentMapper.toDtoList(commentRepository.findAllByItemId(itemDto.getId())));
        }
        return items;
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d not found", itemId)));
        if (item.getOwnerId().equals(userId)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.toDto(itemRepository.save(item));
        } else {
            throw new UserIsNotOwnerException(String.format("User with id %d is not the owner of the item", userId));
        }
    }

    public List<ItemDto> searchAvailableItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.searchAvailableItems(text);
        return items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    // Сортировка и фильтрация происходит на стороне БД
    private Item setLastAndNextBooking(Item item) {
        LocalDateTime now = LocalDateTime.now();
        bookingRepository.getLastItemBooking(item.getId(), now)
                .ifPresent(booking -> item.setLastBooking(BookingMapper.toItemBookingDto(booking)));
        bookingRepository.getNextItemBooking(item.getId(), now)
                .ifPresent(booking -> item.setNextBooking(BookingMapper.toItemBookingDto(booking)));
        return item;
    }

    @Override
    public Comment addComment(Comment comment, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d not found", itemId)));
        User user = userService.getUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository
                .findAllByItemAndBookerIdAndStatusIsAndEndIsBefore(item, userId, BookingStatus.APPROVED, now);
        if (bookings.isEmpty()) {
            throw new UserHasNotBookedItem("You need to finish at least one booking to leave a comment");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now);
        return commentRepository.save(comment);
    }
}
