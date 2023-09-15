package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.user.CreateUserDTO;
import ru.practicum.mainservice.dto.user.UserDTO;
import ru.practicum.mainservice.model.User;

import java.util.List;

public interface UserService {
    UserDTO createUser(CreateUserDTO newUser);

    User getUserById(int userId);

    void deleteUserById(int userId);

    List<UserDTO> getUsers(int from, int size);

    List<UserDTO> getUsers(List<Integer> ids);
}
