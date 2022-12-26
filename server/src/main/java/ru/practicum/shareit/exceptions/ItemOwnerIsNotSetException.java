package ru.practicum.shareit.exceptions;

public class ItemOwnerIsNotSetException extends IllegalArgumentException {
    public ItemOwnerIsNotSetException(String s) {
        super(s);
    }
}
