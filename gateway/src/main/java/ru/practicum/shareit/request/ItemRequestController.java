package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public Object createRequest(@RequestHeader(USER_ID_HEADER) @NotNull Long requesterId,
                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public Object getAllUserRequests(@RequestHeader(USER_ID_HEADER) Long requesterId) {
        return itemRequestClient.getAllUserRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public Object getRequestById(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                 @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public Object getAllRequests(@RequestHeader(USER_ID_HEADER) @NotNull Long userId,
                                 @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                 @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}
