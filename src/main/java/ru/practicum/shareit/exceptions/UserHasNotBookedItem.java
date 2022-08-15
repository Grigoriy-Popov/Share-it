package ru.practicum.shareit.exceptions;

public class UserHasNotBookedItem extends RuntimeException {
    public UserHasNotBookedItem(String message) {
        super(message);
    }
}
