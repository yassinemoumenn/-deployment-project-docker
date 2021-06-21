package com.example.demo.services;

import com.example.demo.Dto.UserDto;
import com.example.demo.models.User;

import java.util.List;


public interface UserService {
    UserDto createUser(UserDto userDto);
    List<UserDto> getUsers(int page, int limit);
    UserDto getUserByUserId(String userId);
    UserDto updateUser(String userId, UserDto userDto);
    void deleteUser(String userId);
    UserDto updateUserAccount(String userId, UserDto userDto);
}
