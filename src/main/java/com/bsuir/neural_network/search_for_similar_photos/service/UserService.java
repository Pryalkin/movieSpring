package com.bsuir.neural_network.search_for_similar_photos.service;

import com.bsuir.neural_network.search_for_similar_photos.dto.UserDTO;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.PasswordException;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.model.user.User;

public interface UserService {

    void registration(UserDTO userDTO) throws UsernameExistException, PasswordException;
    User findByUsername(String username) throws UsernameExistException;

    void subscribe(String usernameWithToken) throws UsernameExistException;
}
