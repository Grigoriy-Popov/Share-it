package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * // TODO .
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank (message = "Name can't be empty")
    private String name;
    @Email (message = "Invalid email format")
    @NotBlank (message = "Email can't be empty")
    private String email;
}
