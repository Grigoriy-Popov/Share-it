package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_ID_HEADER) Long requesterId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.fromDto(itemRequestDto);
        return ItemRequestMapper.toDto(itemRequestService.createRequest(itemRequest, requesterId));
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader(USER_ID_HEADER) Long requesterId) {
        return ItemRequestMapper.toDtoList(itemRequestService.getAllUserRequests(requesterId));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return ItemRequestMapper.toDto(itemRequestService.getRequestById(requestId, userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(name = "from") Integer from,
                                               @RequestParam(name = "size") Integer size) {
        return ItemRequestMapper.toDtoList(itemRequestService.getAllRequests(userId, from, size));
    }
}
