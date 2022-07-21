package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * // TODO .
 */
@Data
public class User {
    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
}
