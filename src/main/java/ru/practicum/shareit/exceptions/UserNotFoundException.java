package ru.practicum.shareit.exceptions;

public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException(String s) {
        super(s);
    }
}
