package com.bsuir.neural_network.search_for_similar_photos.controller;

import com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer;
import com.bsuir.neural_network.search_for_similar_photos.dto.LoginUserDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.PersonDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.util.HttpResponse;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.PasswordException;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.service.PersonService;
import com.bsuir.neural_network.search_for_similar_photos.utility.JWTTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer.USER_SUCCESSFULLY_REGISTERED;
import static com.bsuir.neural_network.search_for_similar_photos.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;
    private final JWTTokenProvider jwtTokenProvider;

    @PostMapping("/registration")
    public ResponseEntity<HttpResponse> registration(@RequestParam String name,
                                                     @RequestParam String surname,
                                                     @RequestParam String patronymic,
                                                     @RequestParam String passportSeries,
                                                     @RequestParam String passportNumber,
                                                     @RequestParam String dateOfBirth,
                                                     @RequestParam(value = "file") MultipartFile file,
                                                     HttpServletRequest request) throws UsernameExistException, PasswordException, ParseException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        String usernameWithToken = jwtTokenProvider.getSubject(token);
        personService.registration(name, surname, patronymic, passportSeries, passportNumber, dateOfBirth, file, usernameWithToken);
        log.info("Ура!!! Ура!!! Ура!!!");
        return HttpAnswer.response(CREATED, USER_SUCCESSFULLY_REGISTERED);
    }

}
