package org.tbank.repository;

import org.springframework.stereotype.Repository;
import org.tbank.model.Location;
import java.util.Optional;


@Repository
public class LocationRepository extends SimpleRepository<Location> {

    public Optional<Location> findBySlug(String slug) {
        return storage.values().stream()
                .filter(entity -> entity.getSlug() != null && entity.getSlug().equals(slug))
                .findFirst();
    }
}
