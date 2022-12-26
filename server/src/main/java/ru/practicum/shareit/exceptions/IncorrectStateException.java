package ru.practicum.shareit.exceptions;

public class IncorrectStateException extends IllegalArgumentException {
    public IncorrectStateException(String s) {
        super(s);
    }
}
