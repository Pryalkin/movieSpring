package com.bsuir.neural_network.search_for_similar_photos.service;

import com.bsuir.neural_network.search_for_similar_photos.exception.model.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface PersonService {
    void registration(String name, String surname, String patronymic, String passportSeries, String passportNumber, String dateOfBirth, MultipartFile file, String usernameWithToken) throws UsernameExistException, ParseException, IOException;
}
