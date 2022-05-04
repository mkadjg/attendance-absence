package com.absence.repositories;

import com.absence.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {

    @Query("select u from Users u where u.username =:username")
    Optional<Users> findByUsername(String username);

}
