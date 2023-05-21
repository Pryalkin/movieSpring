package com.bsuir.moviesearchsystem.service.impl;

import com.bsuir.moviesearchsystem.dto.UserDTO;
import com.bsuir.moviesearchsystem.entity.user.User;
import com.bsuir.moviesearchsystem.entity.user.UserPrincipal;
import com.bsuir.moviesearchsystem.exception.model.PasswordException;
import com.bsuir.moviesearchsystem.exception.model.UsernameExistException;
import com.bsuir.moviesearchsystem.repository.UserRepository;
import com.bsuir.moviesearchsystem.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.bsuir.moviesearchsystem.constant.UserImplConstant.*;
import static com.bsuir.moviesearchsystem.enumeration.Role.ROLE_ADMIN;
import static com.bsuir.moviesearchsystem.enumeration.Role.ROLE_USER;

@Service
@Qualifier("userDetailsService")
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
        UserPrincipal userPrincipal = new UserPrincipal(user);
        log.info(FOUND_USER_BY_USERNAME + username);
        return userPrincipal;
    }

    @Override
    public void registration(UserDTO userDTO) throws UsernameExistException, PasswordException {
        validateNewUsernameAndPassword(userDTO);
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(encodePassword(userDTO.getPassword()));
        if (userRepository.findAll().isEmpty()) {
            user.setRole(ROLE_ADMIN.name());
            user.setAuthorities(ROLE_ADMIN.getAuthorities());
        } else {
            user.setRole(ROLE_USER.name());
            user.setAuthorities(ROLE_USER.getAuthorities());
        }
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) throws UsernameExistException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameExistException(USERNAME_ALREADY_EXISTS));
    }

    private void validateNewUsernameAndPassword(UserDTO userDTO) throws UsernameExistException, PasswordException {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()){
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if (!userDTO.getPassword().equals(userDTO.getPassword2())){
            throw new PasswordException(PASSWORD_IS_NOT_VALID);
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
