package com.scholarly.auth.repository;

import com.scholarly.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(com.scholarly.auth.model.Role role);
    List<User> findByRole(com.scholarly.auth.model.Role role);
}
