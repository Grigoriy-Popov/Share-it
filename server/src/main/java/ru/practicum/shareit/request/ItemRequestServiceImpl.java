package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest, Long requesterId) {
        User user = userService.getUserById(requesterId);
        itemRequest.setRequester(user);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getAllUserRequests(Long userId) {
        userService.checkExistenceById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
            itemRequest.setItems(new HashSet<>(items));
        }
        return itemRequests;
    }

    @Override
    public List<ItemRequest> getAllRequests(Long userId, int from, int size) {
        userService.checkExistenceById(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by("created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNot(userId, page).getContent();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
            itemRequest.setItems(new HashSet<>(items));
        }
        return itemRequests;
    }

    @Override
    public ItemRequest getRequestById(Long requestId, Long userId) {
        userService.checkExistenceById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id %d not found", requestId)));
        List<Item> items = itemRepository.findAllByItemRequest(itemRequest);
        itemRequest.setItems(new HashSet<>(items));
        return itemRequest;
    }
}
