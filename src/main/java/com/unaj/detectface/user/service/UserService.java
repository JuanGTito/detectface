package com.unaj.detectface.user.service;

import com.unaj.detectface.user.dto.UserCreateDto;
import com.unaj.detectface.user.dto.UserDto;
import com.unaj.detectface.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto findByEmail(String email);
    UserDto create(UserCreateDto userCreateDto);
    UserDto update(Long id, UserUpdateDto userUpdateDto);
    void delete(Long id);
}
