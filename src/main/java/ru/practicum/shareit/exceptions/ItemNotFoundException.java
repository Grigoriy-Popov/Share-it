package ru.practicum.shareit.exceptions;

public class ItemNotFoundException extends IllegalArgumentException {
    public ItemNotFoundException(String s) {
        super(s);
    }
}
