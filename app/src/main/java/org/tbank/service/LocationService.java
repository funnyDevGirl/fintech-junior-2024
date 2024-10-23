package org.tbank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.dto.locations.LocationDTO;
import org.tbank.dto.locations.LocationUpdateDTO;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Location;
import org.tbank.repository.LocationJpaRepository;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    @Value("${locations-url}")
    private String apiUrl;

    private final LocationMapper mapper;
    private final LocationJpaRepository repository;
    private final RestTemplate restTemplate;


    public List<LocationDTO> getAll() {
        List<Location> locations = repository.findAll();
        log.info("{} locations were extracted from the database", locations.size());

        return locations.stream().map(mapper::map).toList();
    }

    public LocationDTO findById(Long id) {
        Location location = repository.findByIdWithEvents(id).orElseThrow(
                () -> new ResourceNotFoundException("Location with id: " + id + " not found"));

        log.info("Location with id: {} found in DB", id);

        return mapper.map(location);
    }

    public LocationDTO create(LocationCreateDTO locationCreateDTO) {
        Location location = mapper.map(locationCreateDTO);
        repository.save(location);

        return mapper.map(location);
    }

    public LocationDTO update(LocationUpdateDTO locationUpdateDTO, Long id) {
        Location location = repository.findByIdWithEvents(id).orElseThrow(
                () -> new ResourceNotFoundException("Location with id: " + id + " not found"));

        mapper.update(locationUpdateDTO, location);
        repository.save(location);

        return mapper.map(location);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Location with id: " + id + " not found");
        }
        repository.deleteById(id);
    }

    public List<LocationCreateDTO> fetchLocations() {
        ResponseEntity<LocationCreateDTO[]> responseEntity = restTemplate.exchange(
                apiUrl, HttpMethod.GET, null, LocationCreateDTO[].class);

        LocationCreateDTO[] locations = responseEntity.getBody();

        return locations != null ? Arrays.asList(locations) : List.of();
    }
}
