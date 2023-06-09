package com.bsuir.neural_network.search_for_similar_photos.controller;

import com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer;
import com.bsuir.neural_network.search_for_similar_photos.dto.LoginUserDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.answer.LoginUserAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.util.HttpResponse;
import com.bsuir.neural_network.search_for_similar_photos.exception.ExceptionHandling;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.PasswordException;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.model.user.User;
import com.bsuir.neural_network.search_for_similar_photos.model.user.UserPrincipal;
import com.bsuir.neural_network.search_for_similar_photos.service.UserService;
import com.bsuir.neural_network.search_for_similar_photos.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer.USER_SUCCESSFULLY_REGISTERED;
import static com.bsuir.neural_network.search_for_similar_photos.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController extends ExceptionHandling {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;

    @PostMapping("/registration")
    public ResponseEntity<HttpResponse> registration(@RequestBody LoginUserDTO loginUserDTO) throws UsernameExistException, PasswordException {
        userService.registration(loginUserDTO);
        return HttpAnswer.response(CREATED, USER_SUCCESSFULLY_REGISTERED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserAnswerDTO> login(@RequestBody LoginUserDTO loginUserDTO) throws UsernameExistException {
        authenticate(loginUserDTO.getUsername(), loginUserDTO.getPassword());
        User loginUser = userService.findByUsername(loginUserDTO.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        LoginUserAnswerDTO loginUserAnswerDTO = new LoginUserAnswerDTO();
        loginUserAnswerDTO.setUsername(loginUser.getUsername());
        loginUserAnswerDTO.setRole(loginUser.getRole());
        loginUserAnswerDTO.setAuthorities(loginUser.getAuthorities());
        return new ResponseEntity<>(loginUserAnswerDTO, jwtHeader, OK);
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

}
