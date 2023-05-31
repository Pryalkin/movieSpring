package com.bsuir.neural_network.search_for_similar_photos.service;

import com.bsuir.neural_network.search_for_similar_photos.dto.answer.HistoryAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.dto.answer.ImageAnswerDTO;
import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    void upload(String keywords, MultipartFile file) throws IOException;

    List<ImageAnswerDTO> getAll();

    List<ImageAnswerDTO> similarImageSearch(String searchScore, String keywords, MultipartFile file, String usernameWithToken) throws IOException, UsernameExistException;

    List<ImageAnswerDTO> getAllImageForUser(String usernameWithToken) throws UsernameExistException;

    void imageSaveIForUser(Long id, String usernameWithToken) throws UsernameExistException;

    List<HistoryAnswerDTO> getAllHistoryForUser(String usernameWithToken) throws UsernameExistException;

    List<ImageAnswerDTO> findImages(String str) throws UsernameExistException;
}
