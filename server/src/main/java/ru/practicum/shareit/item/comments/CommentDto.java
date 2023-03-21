package ru.practicum.shareit.item.comments;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;

    private String text;

    private ItemDto item;

    private String authorName;

    private LocalDateTime created;
}
