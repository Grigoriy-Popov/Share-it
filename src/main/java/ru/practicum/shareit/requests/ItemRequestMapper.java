package ru.practicum.shareit.requests;

import ru.practicum.shareit.item.ItemMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null ?
                        new HashSet<>(ItemMapper.toDtoList(itemRequest.getItems())) : Collections.emptySet())
                .build();
    }

    public static ItemRequest fromDto(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .build();
    }

    public static List<ItemRequestDto> toDtoList(List<ItemRequest> requests) {
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
