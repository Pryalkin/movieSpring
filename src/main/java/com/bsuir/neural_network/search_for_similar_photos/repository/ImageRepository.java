package com.bsuir.neural_network.search_for_similar_photos.repository;

import com.bsuir.neural_network.search_for_similar_photos.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByName(String name);
}
