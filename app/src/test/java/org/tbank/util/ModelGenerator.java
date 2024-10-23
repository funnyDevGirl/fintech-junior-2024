package org.tbank.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;
import org.tbank.model.Event;
import org.tbank.model.Location;
import java.util.concurrent.TimeUnit;

@Getter
@Component
@RequiredArgsConstructor
public class ModelGenerator {
    private final Faker faker;

    private Model<Event> eventModel;
    private Model<Location> locationModel;

    @PostConstruct
    private void init() {

        locationModel = Instancio.of(Location.class)
                .ignore(Select.field(Location::getId))
                .supply(Select.field(Location::getSlug), () -> faker.internet().slug())
                .supply(Select.field(Location::getName), () -> faker.name().title())
                .ignore(Select.field(Location::getEvents))
                .toModel();

        eventModel = Instancio.of(Event.class)
                .ignore(Select.field(Event::getId))
                .supply(Select.field(Event::getName), () -> faker.name().title())
                .ignore(Select.field(Event::getPlace))
                .supply(Select.field(Event::getDate), () -> faker.date().future(5, TimeUnit.DAYS).toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate())
                .toModel();
    }
}
