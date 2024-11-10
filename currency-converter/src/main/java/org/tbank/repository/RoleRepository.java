package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbank.model.Role;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role AS r WHERE r.name IN :roleNames")
    Set<Role> findByNameIn(@Param("roleNames") Set<String> roleNames);

    @Query("SELECT r FROM Role AS r LEFT JOIN FETCH r.users WHERE r.name = :name")
    Optional<Role> findByNameWithEagerUpload(@Param("name") String name);

    @Query("SELECT r FROM Role AS r LEFT JOIN FETCH r.users WHERE r.id = :id")
    Optional<Role> findByIdWithEagerUpload(@Param("id") Long id);
}
