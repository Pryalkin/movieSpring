package com.bsuir.moviesearchsystem.service.impl;

import com.bsuir.moviesearchsystem.dto.MovieDTO;
import com.bsuir.moviesearchsystem.dto.answer.MovieAnswerDTO;
import com.bsuir.moviesearchsystem.entity.Movie;
import com.bsuir.moviesearchsystem.entity.Rating;
import com.bsuir.moviesearchsystem.enumeration.Genre;
import com.bsuir.moviesearchsystem.enumeration.Level;
import com.bsuir.moviesearchsystem.exception.model.GenreException;
import com.bsuir.moviesearchsystem.exception.model.LevelException;
import com.bsuir.moviesearchsystem.repository.MovieRepository;
import com.bsuir.moviesearchsystem.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bsuir.moviesearchsystem.constant.ExceptionConstant.WRONG_MOVIE_GENRE;
import static com.bsuir.moviesearchsystem.constant.ExceptionConstant.WRONG_MOVIE_STATUS;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<MovieAnswerDTO> registration(MovieDTO movieDTO) throws LevelException, GenreException {
        Movie movie = new Movie();
        if (isValidLevel(movieDTO.getLevel())) {
            Level level = Level.valueOf(movieDTO.getLevel());
            movie.setLevel(level.name());
        } else
            throw new LevelException(WRONG_MOVIE_STATUS);
        if (isValidGenre(movieDTO.getGenre())) {
            Genre genre = Genre.valueOf(movieDTO.getGenre());
            movie.setGenre(genre.name());
        } else
            throw new GenreException(WRONG_MOVIE_GENRE);
        movie.setName(movieDTO.getName());
        movie.setCountry(movieDTO.getCountry());
        movie.setDate(new Date());
        movieRepository.save(movie);
        return getAll();
    }

    @Override
    public List<MovieAnswerDTO> getAll() {
        return movieRepository.findAll().stream()
                .map(this::createMovieAnswerDTO).collect(Collectors.toList());
    }

    private MovieAnswerDTO createMovieAnswerDTO(Movie movie) {
        MovieAnswerDTO movieAnswerDTO = new MovieAnswerDTO();
        movieAnswerDTO.setId(movie.getId());
        movieAnswerDTO.setName(movie.getName());
        movieAnswerDTO.setGenre(movie.getGenre());
        movieAnswerDTO.setCountry(movie.getCountry());
        movieAnswerDTO.setLevel(movie.getLevel());
        movieAnswerDTO.setDate(movie.getDate());
        Double rating = movie.getRatings().stream().mapToDouble(Rating::getGrade).average().orElse(0.0);
        movieAnswerDTO.setRatings(rating);
        return movieAnswerDTO;
    }

    @Override
    public MovieAnswerDTO get(Long id) {
        return createMovieAnswerDTO(movieRepository.findById(id).get());
    }

    @Override
    public List<MovieAnswerDTO> getMoviesFind(String find) {
        if(find.equals("")){
            return getAll();
        } else {
            List<Movie> movies = movieRepository.findAll()
                    .stream().filter(movie -> movie.getName().contains(find)).toList();
            return movies.stream().map(this::createMovieAnswerDTO).collect(Collectors.toList());
        }
    }

    private static boolean isValidLevel(String levelName) {
        for (Level level : Level.values()) {
            if (level.name().equals(levelName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidGenre(String genreName) {
        for (Genre genre : Genre.values()) {
            if (genre.name().equals(genreName)) {
                return true;
            }
        }
        return false;
    }
}
