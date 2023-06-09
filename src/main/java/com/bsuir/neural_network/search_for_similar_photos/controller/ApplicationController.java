package com.bsuir.neural_network.search_for_similar_photos.controller;

import com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer;
import com.bsuir.neural_network.search_for_similar_photos.dto.util.HttpResponse;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.PasswordException;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.service.ApplicationService;
import com.bsuir.neural_network.search_for_similar_photos.utility.JWTTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.FORWARD_SLASH;
import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.USER_FOLDER;
import static com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer.USER_SUCCESSFULLY_REGISTERED;
import static com.bsuir.neural_network.search_for_similar_photos.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/application")
@AllArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JWTTokenProvider jwtTokenProvider;

    @PostMapping("/registration")
    public ResponseEntity<HttpResponse> registration(@RequestParam Long id,
                                                     HttpServletRequest request) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        String usernameWithToken = jwtTokenProvider.getSubject(token);
        applicationService.registration(id, usernameWithToken);
        return HttpAnswer.response(CREATED, USER_SUCCESSFULLY_REGISTERED);
    }

    @GetMapping(path = "/{username}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@PathVariable("username") String username,
                                               @PathVariable("fileName") String fileName,
                                               HttpServletResponse response) throws IOException {
        // Проверяем, есть ли файл
        Path file = Paths.get(USER_FOLDER + FORWARD_SLASH + username + FORWARD_SLASH + fileName);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        // Устанавливаем заголовки для ответа
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Читаем байты файла и отправляем в браузер
        byte[] data = Files.readAllBytes(file);
        return ResponseEntity.ok().body(data);
    }
}
