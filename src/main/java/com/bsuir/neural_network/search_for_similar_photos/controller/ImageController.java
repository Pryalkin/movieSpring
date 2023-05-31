package com.bsuir.neural_network.search_for_similar_photos.controller;

import com.bsuir.neural_network.search_for_similar_photos.constant.HttpAnswer;
import com.bsuir.neural_network.search_for_similar_photos.dto.answer.HistoryAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.answer.ImageAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.util.HttpResponse;
import com.bsuir.neural_network.search_for_similar_photos.exception.ExceptionHandling;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import com.bsuir.neural_network.search_for_similar_photos.service.ImageService;
import com.bsuir.neural_network.search_for_similar_photos.utility.JWTTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
import static com.bsuir.neural_network.search_for_similar_photos.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
@Slf4j
public class ImageController extends ExceptionHandling {

    private final ImageService imageService;
    private final JWTTokenProvider jwtTokenProvider;

    @PostMapping("/upload")
    public ResponseEntity<HttpResponse> upload(@RequestParam String keywords,
                                               @RequestParam(value = "file") MultipartFile file) throws IOException {
        imageService.upload(keywords, file);
        return HttpAnswer.response(CREATED, IMAGE_DELIVERED_SUCCESSFULLY);
    }

    @GetMapping(path = "/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/user/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getImageForHistory(@PathVariable("fileName") String fileName,
                                     @PathVariable("username") String username) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + FORWARD_SLASH + username + FORWARD_SLASH + fileName));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ImageAnswerDTO>> getAll(){
        return new ResponseEntity<>(imageService.getAll(), OK);
    }

    @GetMapping("/user/getAll")
    public ResponseEntity<List<ImageAnswerDTO>> getAllImageForUser(HttpServletRequest request) throws UsernameExistException {
        String usernameWithToken = getUsernameWithToken(request);
        return new ResponseEntity<>(imageService.getAllImageForUser(usernameWithToken), OK);
    }

    @GetMapping("/user/history")
    public ResponseEntity<List<HistoryAnswerDTO>> getAllHistoryForUser(HttpServletRequest request) throws UsernameExistException {
        String usernameWithToken = getUsernameWithToken(request);
        return new ResponseEntity<>(imageService.getAllHistoryForUser(usernameWithToken), OK);
    }


    @PostMapping("/similar_image_search")
    public ResponseEntity<List<ImageAnswerDTO>> similarImageSearch(@RequestParam String searchScore,
                                                                   @RequestParam String keywords,
                                                                   @RequestParam(value = "file") MultipartFile file,
                                                                   HttpServletRequest request) throws IOException, UsernameExistException {
        String usernameWithToken = getUsernameWithToken(request);
        double start = System.currentTimeMillis();
        List<ImageAnswerDTO> imageAnswerDTOs = imageService.similarImageSearch(searchScore, keywords, file, usernameWithToken);
        log.info("Время работы запроса: " + (System.currentTimeMillis() - start));
        return new ResponseEntity<>(imageAnswerDTOs, OK);
    }


    @PostMapping("/user/save")
    public ResponseEntity<HttpResponse> upload(@RequestParam Long id,
                                               HttpServletRequest request) throws UsernameExistException {
        imageService.imageSaveIForUser(id, getUsernameWithToken(request));
        return HttpAnswer.response(CREATED, IMAGE_DELIVERED_SUCCESSFULLY);
    }


    public String getUsernameWithToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorizationHeader.substring(TOKEN_PREFIX.length());
        return jwtTokenProvider.getSubject(token);
    }

    @GetMapping("/user/get/images")
    public ResponseEntity<List<ImageAnswerDTO>> findImages(@RequestParam String str) throws UsernameExistException {
        return new ResponseEntity<>(imageService.findImages(str), OK);
    }

}
