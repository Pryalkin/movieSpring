package com.bsuir.moviesearchsystem.entity.user;

import com.bsuir.moviesearchsystem.entity.Movie;
import com.bsuir.moviesearchsystem.entity.Rating;
import com.bsuir.moviesearchsystem.entity.Subscription;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private String username;
    @EqualsAndHashCode.Include
    private String password;
    @EqualsAndHashCode.Include
    private String role;
    @EqualsAndHashCode.Include
    private String[] authorities;
    private Double money;
    @ManyToOne(fetch = FetchType.LAZY)
    private Subscription subscription;
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Movie> movies;
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Rating> ratings;
    public void addMovie(Movie movie){
        movies.add(movie);
        movie.setUser(this);
    }
    public void addRating(Rating rating){
        ratings.add(rating);
        rating.setUser(this);
    }

}
