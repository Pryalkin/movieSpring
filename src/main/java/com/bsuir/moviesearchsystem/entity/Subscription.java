package com.bsuir.moviesearchsystem.entity;

import com.bsuir.moviesearchsystem.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private LocalDate date;
    @EqualsAndHashCode.Include
    private String level;
    @OneToMany(
            mappedBy = "subscription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<User> users;

    public void addUser(User user){
        users.add(user);
        user.setSubscription(this);
    }
}
