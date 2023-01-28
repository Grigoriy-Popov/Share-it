package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserHasNotBookedItem;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService requestService;

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User user = userService.getUserById(userId);
        Item item = ItemMapper.fromDto(itemDto);
        item.setItemRequest(itemDto.getRequestId() != null ?
                requestService.getRequestById(itemDto.getRequestId(), userId) : null);
        item.setOwner(user);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemByIdByUser(Long itemId, Long userId) {
        Item item = getItemFromRepoById(itemId);
        if (item.getOwner().getId().equals(userId)) {
            setLastAndNextBooking(item);
        }
        ItemDto itemDto = ItemMapper.toDto(item);
        itemDto.setComments(CommentMapper.toDtoList(commentRepository.findAllByItemId(item.getId())));
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<ItemDto> items = itemRepository.getAllByOwnerIdOrderById(userId, page).stream()
                .peek(this::setLastAndNextBooking)
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        items.forEach(i -> i.setComments(CommentMapper.toDtoList(commentRepository.findAllByItemId(i.getId()))));
        return items;
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = getItemFromRepoById(itemId);
        if (item.getOwner().getId().equals(userId)) {
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

    @Override
    public List<ItemDto> searchAvailableItemsByKeyword(String text, int from, int size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.searchAvailableItemsByKeyword(text, page);
        return ItemMapper.toDtoList(items);
    }

    // Сортировка и фильтрация происходит на стороне БД
    private void setLastAndNextBooking(Item item) {
        var now = LocalDateTime.now();
        bookingRepository.getLastItemBooking(item.getId(), now)
                .ifPresent(booking -> item.setLastBooking(BookingMapper.toItemBookingDto(booking)));
        bookingRepository.getNextItemBooking(item.getId(), now)
                .ifPresent(booking -> item.setNextBooking(BookingMapper.toItemBookingDto(booking)));
    }

    @Override
    public Comment addComment(Comment comment, Long userId, Long itemId) {
        Item item = getItemFromRepoById(itemId);
        User user = userService.getUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByItemAndBookerIdAndStatusAndEndBefore(item, userId,
                        BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new UserHasNotBookedItem("You need to finish at least one booking to leave a comment");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        return commentRepository.save(comment);
    }

    private Item getItemFromRepoById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d not found", itemId)));
    }
}
