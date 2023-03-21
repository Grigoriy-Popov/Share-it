package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    Long id;
    String name;
    String email;
}

