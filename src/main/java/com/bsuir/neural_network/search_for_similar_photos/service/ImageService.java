package com.bsuir.neural_network.search_for_similar_photos.service;

import com.bsuir.neural_network.search_for_similar_photos.dto.answer.ImageAnswerDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    void upload(String keywords, MultipartFile file) throws IOException;

    List<ImageAnswerDTO> getAll();
}
