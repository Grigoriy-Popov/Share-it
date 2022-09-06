package ru.practicum.shareit.item.comments;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}
