package ru.practicum.shareit.exceptions;

public class ItemIsBookedException extends RuntimeException {
    public ItemIsBookedException(String message) {
        super(message);
    }
}
