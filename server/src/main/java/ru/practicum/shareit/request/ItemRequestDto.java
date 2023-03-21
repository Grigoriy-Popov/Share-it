package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ItemRequestDto {
    private Long id;

    private String description;

    private LocalDateTime created;

    private Set<ItemDto> items;
}
