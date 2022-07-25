package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {
//
//    TODO разобраться как нормально обработать ошибки при некорректной валидаии через аннотации
//
    private Long id;
    @NotBlank (message = "Name can't be empty")
    private String name;
    @NotBlank (message = "Description can't be empty")
    private String description;
    @NotNull
    private Boolean available;
}
