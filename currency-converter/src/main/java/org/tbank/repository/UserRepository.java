package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tbank.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
