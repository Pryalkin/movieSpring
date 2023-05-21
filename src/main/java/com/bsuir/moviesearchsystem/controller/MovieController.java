package com.bsuir.moviesearchsystem.controller;

import com.bsuir.moviesearchsystem.dto.MovieDTO;
import com.bsuir.moviesearchsystem.dto.answer.MovieAnswerDTO;
import com.bsuir.moviesearchsystem.exception.ExceptionHandling;
import com.bsuir.moviesearchsystem.exception.model.GenreException;
import com.bsuir.moviesearchsystem.exception.model.LevelException;

import com.bsuir.moviesearchsystem.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/movie")
@AllArgsConstructor
public class MovieController extends ExceptionHandling {

    private final MovieService movieService;

    @PostMapping("/registration")
    public ResponseEntity<List<MovieAnswerDTO>> registration(@RequestBody MovieDTO movieDTO) throws LevelException, GenreException {
        return new ResponseEntity<>(movieService.registration(movieDTO), OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MovieAnswerDTO>> getAll(){
        return new ResponseEntity<>(movieService.getAll(), OK);
    }

    @GetMapping("/get")
    public ResponseEntity<MovieAnswerDTO> get(@RequestParam Long id){
        return new ResponseEntity<>(movieService.get(id), OK);
    }

    @GetMapping("/getFind")
    public ResponseEntity<List<MovieAnswerDTO>> getMoviesFind(@RequestParam String find){
        return new ResponseEntity<>(movieService.getMoviesFind(find), OK);
    }



}
