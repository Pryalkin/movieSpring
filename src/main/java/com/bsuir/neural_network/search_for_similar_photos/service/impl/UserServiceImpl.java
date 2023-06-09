package com.bsuir.neural_network.search_for_similar_photos.service.impl;

import com.bsuir.neural_network.search_for_similar_photos.dto.LoginUserDTO;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.PasswordException;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.model.user.User;
import com.bsuir.neural_network.search_for_similar_photos.model.user.UserPrincipal;
import com.bsuir.neural_network.search_for_similar_photos.repository.UserRepository;
import com.bsuir.neural_network.search_for_similar_photos.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.bsuir.neural_network.search_for_similar_photos.constant.UserImplConstant.*;
import static com.bsuir.neural_network.search_for_similar_photos.enumeration.Role.ROLE_ADMIN;
import static com.bsuir.neural_network.search_for_similar_photos.enumeration.Role.ROLE_USER;

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
    public void registration(LoginUserDTO loginUserDTO) throws UsernameExistException, PasswordException {
        validateNewUsernameAndPassword(loginUserDTO);
        User user = new User();
        user.setUsername(loginUserDTO.getUsername());
        user.setPassword(encodePassword(loginUserDTO.getPassword()));
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

    private void validateNewUsernameAndPassword(LoginUserDTO loginUserDTO) throws UsernameExistException, PasswordException {
        if (userRepository.findByUsername(loginUserDTO.getUsername()).isPresent()){
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if (!loginUserDTO.getPassword().equals(loginUserDTO.getPassword2())){
            throw new PasswordException(PASSWORD_IS_NOT_VALID);
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

//    private PhoneRepairRequest savePhoto(String username, MultipartFile photo, PhoneRepairRequest phoneRepairRequest) throws IOException {
//        if (photo != null){
//            Path userFolder = Paths.get(USER_FOLDER + username).toAbsolutePath().normalize();
//            if (!Files.exists(userFolder)){
//                Files.createDirectories(userFolder);
//            }
//            String name = generateName();
//            Files.deleteIfExists(Paths.get(userFolder + FORWARD_SLASH + name +  DOT + JPG_EXTENSION));
//            Files.copy(photo.getInputStream(), userFolder.resolve(name + DOT + JPG_EXTENSION), REPLACE_EXISTING);
//            Photo ph = new Photo();
//            ph.setUrlPhoto(setLogoImageUrl(username, name));
//            phoneRepairRequest.addPhoto(ph);
//            return phoneRepairRequest;
//        }
//        return phoneRepairRequest;
//    }
//
//    private String setLogoImageUrl(String username, String name) {
//        return ServletUriComponentsBuilder.fromCurrentContextPath().
//                path(USER_IMAGE_PATH + username + FORWARD_SLASH + name + DOT + JPG_EXTENSION).toUriString();
//    }

}
