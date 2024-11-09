package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbank.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User AS u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithEagerUpload(@Param("email") String email);

    @Query("SELECT u FROM User AS u LEFT JOIN FETCH u.roles WHERE u.id=:id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);
}
