package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    Long id;
    @NotBlank (message = "Name can't be empty")
    String name;
    @Email (message = "Invalid email format")
    @NotBlank (message = "Email can't be empty")
    String email;
}

