package com.bsuir.neural_network.search_for_similar_photos.repository;

import com.bsuir.neural_network.search_for_similar_photos.model.Image;
import com.bsuir.neural_network.search_for_similar_photos.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByName(String name);
    Optional<List<Image>> findByKeywords(Keyword keyword);
}
