package com.bsuir.neural_network.search_for_similar_photos.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
public class PersonDTO {
    private String name;
    private String surname;
    private String patronymic;
    private Date dateOfBirth;
    private String passportSeries;
    private Long passportNumber;
}
