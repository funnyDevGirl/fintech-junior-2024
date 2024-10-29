package org.tbank.repository;

import org.springframework.stereotype.Repository;
import org.tbank.dto.locations.LocationSnapshot;
import org.tbank.model.Location;
import java.util.Optional;


@Repository
public class LocationRepository extends SimpleRepository<Location, LocationSnapshot> {

    public Optional<Location> findBySlug(String slug) {
        return storage.values().stream()
                .filter(entity -> entity.getSlug() != null && entity.getSlug().equals(slug))
                .findFirst();
    }
}
