package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tbank.model.Location;
import java.util.Optional;

@Repository
public interface LocationJpaRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.events WHERE l.id = :id")
    Optional<Location> findByIdWithEvents(@Param("id") Long id);

    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.events WHERE l.slug = :slug")
    Optional<Location> findBySlugWithEvents(@Param("slug") String slug);
}
