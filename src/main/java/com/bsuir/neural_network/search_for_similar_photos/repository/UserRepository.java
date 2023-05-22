package com.bsuir.neural_network.search_for_similar_photos.repository;

import com.bsuir.neural_network.search_for_similar_photos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
