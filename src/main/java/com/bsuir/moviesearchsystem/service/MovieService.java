package com.bsuir.moviesearchsystem.service;

import com.bsuir.moviesearchsystem.dto.MovieDTO;
import com.bsuir.moviesearchsystem.dto.answer.MovieAnswerDTO;
import com.bsuir.moviesearchsystem.exception.model.GenreException;
import com.bsuir.moviesearchsystem.exception.model.LevelException;

import java.util.List;

public interface MovieService {
    List<MovieAnswerDTO> registration(MovieDTO movieDTO) throws LevelException, GenreException;

    List<MovieAnswerDTO> getAll();

    MovieAnswerDTO get(Long id);

    List<MovieAnswerDTO> getMoviesFind(String find);
}
