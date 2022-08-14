package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.fromDto(userDto);
        return UserMapper.toDto(userService.addUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return UserMapper.toDto(userService.getUserById(userId));
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return UserMapper.toDtoList(userService.getAllUsers());
    }

    @PatchMapping("/{userId}")
    public UserDto editUser(@RequestBody UserDto userDto,
                         @PathVariable Long userId) {
        User updateUser = UserMapper.fromDto(userDto);
        return UserMapper.toDto(userService.editUser(updateUser, userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
