package org.tbank.repository;

import org.springframework.stereotype.Component;
import org.tbank.model.Location;

import java.util.Optional;

@Component
public class LocationRepository extends SimpleRepository<Location> {
    // можно добавить доп. методы, специфичные для Location
}
