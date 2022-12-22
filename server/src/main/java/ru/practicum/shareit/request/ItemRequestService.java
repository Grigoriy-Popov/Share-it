package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createRequest(ItemRequest itemRequest, Long requesterId);

    List<ItemRequest> getAllUserRequests(Long userId);

    List<ItemRequest> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequest getRequestById(Long requestId, Long userId);

}
