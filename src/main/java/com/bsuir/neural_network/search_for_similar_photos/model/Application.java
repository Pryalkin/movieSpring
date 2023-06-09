package com.bsuir.neural_network.search_for_similar_photos.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "application")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private String number;
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;
    @EqualsAndHashCode.Include
    private String file;
    @EqualsAndHashCode.Include
    private String status;
    @EqualsAndHashCode.Include
    private Date date;
}
