package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.user.CreateUserDTO;
import ru.practicum.mainservice.dto.user.UserDTO;
import ru.practicum.mainservice.exception.APIException;
import ru.practicum.mainservice.mapper.UserMapper;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.util.OffsetBasedPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APIException(
                        HttpStatus.NOT_FOUND,
                        String.format("User with id=%s was not found", userId),
                        "The required object was not found."
                ));
    }

    @Override
    @Transactional
    public void deleteUserById(int userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsers(int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        return userRepository.findAll(pageable).getContent().stream()
                .map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsers(List<Integer> ids) {
        return userRepository.findAllById(ids).stream().map(userMapper::toDto).collect(Collectors.toList());
    }
}
