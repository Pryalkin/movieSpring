package com.bsuir.moviesearchsystem.dto.answer;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class MovieAnswerDTO {

    private Long id;
    private String name;
    private String genre;
    private String country;
    private String level;
    private Double ratings;
    private Date date;

}
