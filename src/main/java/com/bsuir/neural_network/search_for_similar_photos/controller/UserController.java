package com.bsuir.neural_network.search_for_similar_photos.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.FORWARD_SLASH;
import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.USER_FOLDER;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {


}
