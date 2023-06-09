package com.bsuir.neural_network.search_for_similar_photos.repository;

import com.bsuir.neural_network.search_for_similar_photos.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
