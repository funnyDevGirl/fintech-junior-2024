package org.tbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tbank.model.Event;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventJpaRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByName(String name);

    @Query("SELECT e FROM Event e WHERE e.id IN :eventIds")
    Set<Event> findByIdIn(@Param("eventIds") Set<Long> eventIds);
}
