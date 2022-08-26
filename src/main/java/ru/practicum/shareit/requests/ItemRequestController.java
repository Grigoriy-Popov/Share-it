package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_ID_HEADER) @NotNull Long requesterId,
                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.fromDto(itemRequestDto);
        return ItemRequestMapper.toDto(itemRequestService.createRequest(itemRequest, requesterId));
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserRequests(@RequestHeader(USER_ID_HEADER) @NotNull Long requesterId) {
        return ItemRequestMapper.toDtoList(itemRequestService.getAllUserRequests(requesterId));
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                         @PathVariable Long requestId) {
        return ItemRequestMapper.toDto(itemRequestService.getRequestById(requestId, userId));
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                               @PositiveOrZero @RequestParam(name = "from", required = false,
                                                       defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", required = false,
                                                       defaultValue = "10") Integer size) {
        return ItemRequestMapper.toDtoList(itemRequestService.getAllRequests(userId, from, size));
    }
}
