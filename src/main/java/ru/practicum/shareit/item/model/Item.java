package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * // TODO .
 */
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private User owner = null;
    private ItemRequest request = null;

//    public Item(Long id, String name, String description, Boolean isAvailable, User owner, ItemRequest request) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.isAvailable = isAvailable;
//        this.owner = owner;
//        this.request = request;
//    }

//    public Item(String name, String description, Boolean isAvailable, User owner, ItemRequest request) {
//        this.name = name;
//        this.description = description;
//        this.isAvailable = isAvailable;
//        this.owner = owner;
//        this.request = request;
//    }
}
