package ru.practicum.mainservice.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.filter.PageFilterDTO;
import ru.practicum.mainservice.dto.user.CreateUserDTO;
import ru.practicum.mainservice.dto.user.UserDTO;
import ru.practicum.mainservice.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> getUsers(
            PageFilterDTO pageableData,
            @RequestParam(value = "ids", required = false) List<Integer> ids
    ) {
        log.info("Запрос на получение пользователей ids={} data={}", ids, pageableData);
        List<UserDTO> users = ids == null || ids.isEmpty()
                ? userService.getUsers(pageableData.getFrom(), pageableData.getSize())
                : userService.getUsers(ids);
        log.info("Получено пользователей {}", users.size());
        return users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody @Valid CreateUserDTO dto) {
        log.info("Получен запрос на создание пользователя data={}", dto);
        UserDTO user = userService.createUser(dto);
        log.info("Успешное создание пользователя data={}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @PositiveOrZero int userId) {
        log.info("Запрос на удаление пользователя id={}", userId);
        userService.deleteUserById(userId);
        log.info("Пользователь id={} успешно удален", userId);
    }
}
