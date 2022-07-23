package ru.practicum.shareit.exceptions;

public class DuplicateEmailException extends IllegalArgumentException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
