package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public Object createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(value = USER_ID_HEADER) @NotNull Long userId) {
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Object getItemById(@PathVariable Long itemId,
                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public Object getAllUserItems(@RequestHeader(USER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return itemClient.getAllUserItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public Object editItem(@RequestHeader(USER_ID_HEADER) Long userId,
                            @PathVariable Long itemId,
                            @RequestBody ItemDto itemDto) {
        return itemClient.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public Object searchAvailableItemsByKeyword(@RequestParam(defaultValue = "") String text,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return itemClient.searchAvailableItemsByKeyword(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public Object addComment(@Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(value = USER_ID_HEADER) @NotNull Long userId,
                                 @PathVariable Long itemId) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
