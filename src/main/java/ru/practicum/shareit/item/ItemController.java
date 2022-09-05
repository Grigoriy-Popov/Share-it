package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ItemOwnerIsNotSetException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentMapper;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        if (userId == null) {
            throw new ItemOwnerIsNotSetException("User not specified in request");
        }
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", required = false,
                                                 defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", required = false,
                                                 defaultValue = "10") Integer size) {
        return itemService.getAllUserItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader(USER_ID_HEADER) Long userId,
                            @PathVariable Long itemId,
                            @RequestBody ItemDto itemDto) {
            return itemService.editItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItemsByKeyword(@RequestParam(defaultValue = "") String text,
                                                       @PositiveOrZero @RequestParam(name = "from", required = false,
                                                               defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", required = false,
                                                               defaultValue = "10") Integer size) {
        return itemService.searchAvailableItemsByKeyword(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(value = USER_ID_HEADER, required = false) Long userId,
                                 @PathVariable Long itemId) {
        if (userId == null) {
            throw new ItemOwnerIsNotSetException("User not specified in request");
        }
        Comment comment = CommentMapper.fromDto(commentDto);
        return CommentMapper.toDto(itemService.addComment(comment, userId, itemId));
    }
}
