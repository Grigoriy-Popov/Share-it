package ru.practicum.shareit.exceptions;

public class UserIsNotOwnerException extends IllegalArgumentException {
    public UserIsNotOwnerException(String s) {
        super(s);
    }
}
