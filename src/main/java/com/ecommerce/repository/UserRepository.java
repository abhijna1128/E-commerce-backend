package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repository interface for User entity
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
