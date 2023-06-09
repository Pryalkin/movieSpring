package com.bsuir.neural_network.search_for_similar_photos.controller;

import com.bsuir.neural_network.search_for_similar_photos.model.SampleApplication;
import com.bsuir.neural_network.search_for_similar_photos.service.SampleApplicationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.FORWARD_SLASH;
import static com.bsuir.neural_network.search_for_similar_photos.constant.FileConstant.USER_FOLDER;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/sa")
@AllArgsConstructor
public class SampleApplicationController {

    private final SampleApplicationService sampleApplicationService;

    @GetMapping("/getAll")
    public ResponseEntity<List<SampleApplication>> getAll(){
        List<SampleApplication> sas = sampleApplicationService.getAll();
        return new ResponseEntity<>(sas, OK);
    }

    @GetMapping(path = "/file/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName,
                                               HttpServletResponse response) throws IOException {
        // Проверяем, есть ли файл
        Path file = Paths.get(USER_FOLDER + FORWARD_SLASH + fileName);
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
