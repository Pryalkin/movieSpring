package com.bsuir.moviesearchsystem.entity;

import com.bsuir.moviesearchsystem.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private String name;
    @EqualsAndHashCode.Include
    private String genre;
    @EqualsAndHashCode.Include
    private String country;
    @EqualsAndHashCode.Include
    private String level;
    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Rating> ratings = new HashSet<>();
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void addRating(Rating rating){
        ratings.add(rating);
        rating.setMovie(this);
    }
}
