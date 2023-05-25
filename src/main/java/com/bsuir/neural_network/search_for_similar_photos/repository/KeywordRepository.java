package com.bsuir.neural_network.search_for_similar_photos.repository;

import com.bsuir.neural_network.search_for_similar_photos.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByWord(String word);
}
