package com.bsuir.moviesearchsystem.service;

import com.bsuir.moviesearchsystem.dto.UserDTO;
import com.bsuir.moviesearchsystem.entity.user.User;
import com.bsuir.moviesearchsystem.exception.model.PasswordException;
import com.bsuir.moviesearchsystem.exception.model.UsernameExistException;

public interface UserService {

    void registration(UserDTO userDTO) throws UsernameExistException, PasswordException;
    User findByUsername(String username) throws UsernameExistException;

}
