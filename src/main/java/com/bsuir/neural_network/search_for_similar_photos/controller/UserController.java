package com.bsuir.neural_network.search_for_similar_photos.controller;

import com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer;
import com.bsuir.neural_network.search_for_similar_photos.dto.UserDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.answer.ImageAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.util.HttpResponse;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.PasswordException;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.service.UserService;
import com.bsuir.neural_network.search_for_similar_photos.utility.JWTTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer.USER_SUCCESSFULLY_REGISTERED;
import static com.bsuir.neural_network.search_for_similar_photos.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;


    @PostMapping("/subscribe")
    public ResponseEntity<HttpResponse> subscribe(HttpServletRequest request) throws UsernameExistException {
        String usernameWithToken = getUsernameWithToken(request);
        userService.subscribe(usernameWithToken);
        return HttpAnswer.response(CREATED, USER_SUCCESSFULLY_REGISTERED);
    }

    public String getUsernameWithToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return jwtTokenProvider.getSubject(token);
    }

}
