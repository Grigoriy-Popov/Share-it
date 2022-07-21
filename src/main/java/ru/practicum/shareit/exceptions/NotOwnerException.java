package ru.practicum.shareit.exceptions;

public class NotOwnerException extends IllegalArgumentException {
    public NotOwnerException(String s) {
        super(s);
    }
}
