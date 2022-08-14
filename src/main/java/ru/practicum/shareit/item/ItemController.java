package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ItemOwnerIsNotSetException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new ItemOwnerIsNotSetException("User not specified in request");
        }
        return itemService.addItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllUsersItems(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId,
                            @RequestBody ItemDto itemDto) {
            return itemService.editItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestParam(defaultValue = "") String text) {
        return itemService.searchAvailableItems(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                 @PathVariable Long itemId) {
        if (userId == null) {
            throw new ItemOwnerIsNotSetException("User not specified in request");
        }
        Comment comment = CommentMapper.fromDto(commentDto);
        return CommentMapper.toDto(itemService.addComment(comment, userId, itemId));
    }
}
