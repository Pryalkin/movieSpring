package com.bsuir.moviesearchsystem.controller;

import com.bsuir.moviesearchsystem.constant.HttpAnswer;
import com.bsuir.moviesearchsystem.dto.HttpResponse;
import com.bsuir.moviesearchsystem.dto.answer.LoginUserAnswerDTO;
import com.bsuir.moviesearchsystem.dto.UserDTO;
import com.bsuir.moviesearchsystem.entity.user.User;
import com.bsuir.moviesearchsystem.entity.user.UserPrincipal;
import com.bsuir.moviesearchsystem.exception.ExceptionHandling;
import com.bsuir.moviesearchsystem.exception.model.PasswordException;
import com.bsuir.moviesearchsystem.exception.model.UsernameExistException;
import com.bsuir.moviesearchsystem.service.UserService;
import com.bsuir.moviesearchsystem.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bsuir.moviesearchsystem.constant.HttpAnswer.USER_SUCCESSFULLY_REGISTERED;
import static com.bsuir.moviesearchsystem.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class AuthController extends ExceptionHandling {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;


    @PostMapping("/registration")
    public ResponseEntity<HttpResponse> registration(@RequestBody UserDTO userDTO) throws UsernameExistException, PasswordException {
        userService.registration(userDTO);
        return HttpAnswer.response(CREATED, USER_SUCCESSFULLY_REGISTERED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserAnswerDTO> login(@RequestBody UserDTO userDTO) throws UsernameExistException {
        authenticate(userDTO.getUsername(), userDTO.getPassword());
        User loginUser = userService.findByUsername(userDTO.getUsername());
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
