package org.tbank.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Location;
import org.tbank.observers.LoggingObserver;
import org.tbank.repository.LocationRepository;
import org.tbank.service.LocationService;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InitLocationsCommand implements Command {

    private final LocationRepository locationRepository;
    private final LocationService locationService;
    private final LocationMapper locationMapper;

    @Override
    public void execute() {

        LoggingObserver<Location> locationObserver = new LoggingObserver<>();
        locationRepository.addObserver(locationObserver);

        try {
            List<LocationCreateDTO> locationCreateDTOS = locationService.fetchLocations();

            log.debug("Количество локаций, полученный от API: {}", locationCreateDTOS.size());

            if (!locationCreateDTOS.isEmpty()) {

                log.info("Received {} locations", locationCreateDTOS.size());

                locationCreateDTOS.forEach(location -> {
                    var loc = locationMapper.map(location);
                    locationRepository.save(loc);
                    log.info("Saved location: {}", loc);
                });

                log.info("Locations initialization completed successfully.");

            } else {
                log.warn("No locations found in API response");
            }

        } catch (Exception e) {
            log.error("Error when getting a list of locations", e);
        }
    }
}
