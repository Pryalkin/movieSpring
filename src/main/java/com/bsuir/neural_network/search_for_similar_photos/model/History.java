package com.bsuir.neural_network.search_for_similar_photos.model;

import com.bsuir.neural_network.search_for_similar_photos.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.util.Lazy;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @EqualsAndHashCode.Include
    private String url;
    @EqualsAndHashCode.Include
    private String[] words;

}
