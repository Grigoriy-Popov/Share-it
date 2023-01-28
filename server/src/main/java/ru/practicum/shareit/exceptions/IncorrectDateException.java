package ru.practicum.shareit.exceptions;

public class IncorrectDateException extends IllegalArgumentException {
    public IncorrectDateException(String message) {
        super(message);
    }
}
