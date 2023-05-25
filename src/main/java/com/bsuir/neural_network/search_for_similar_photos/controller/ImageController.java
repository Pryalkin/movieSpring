package com.bsuir.neural_network.search_for_similar_photos.controller;

import com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer;
import com.bsuir.neural_network.search_for_similar_photos.dto.answer.ImageAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.util.HttpResponse;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.NoRightException;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.FORWARD_SLASH;
import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.USER_FOLDER;
import static com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer.IMAGE_DELIVERED_SUCCESSFULLY;
import static com.bsuir.neural_network.search_for_similar_photos.controller.security.ValidUsernameSecurity.checkUsernameForValidity;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<HttpResponse> upload(@RequestParam String keywords,
                                               @RequestParam(value = "file") MultipartFile file) throws IOException {
        imageService.upload(keywords, file);
        return HttpAnswer.response(CREATED, IMAGE_DELIVERED_SUCCESSFULLY);
    }

    @GetMapping(path = "/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + FORWARD_SLASH + fileName));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ImageAnswerDTO>> getAll(){
        return new ResponseEntity<>(imageService.getAll(), OK);
    }


}
