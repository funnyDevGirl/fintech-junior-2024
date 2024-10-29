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
import org.tbank.dto.locations.LocationSnapshot;
import org.tbank.dto.locations.LocationUpdateDTO;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.mapper.LocationMapper;
import org.tbank.repository.LocationRepository;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    @Value("${locations-url}")
    private String apiUrl;

    private final LocationMapper mapper;
    private final LocationRepository repository;
    private final RestTemplate restTemplate;


    public List<LocationDTO> getAll() {
        var locations = repository.findAll();

        return locations.stream().map(mapper::map).toList();
    }

    public LocationDTO findById(Long id) {
        var location = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Location With Id: " + id + " Not Found"));

        return mapper.map(location);
    }

    public LocationDTO create(LocationCreateDTO locationCreateDTO) {
        var location = mapper.map(locationCreateDTO);
        repository.save(location);

        return mapper.map(location);
    }

    public LocationDTO update(LocationUpdateDTO locationUpdateDTO, Long id) {
        var location = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Location With Id: " + id + " Not Found"));

        LocationSnapshot snapshot = location.createSnapshot();
        log.info("Snapshot of location '{}' is saved before the update", snapshot);
        repository.saveSnapshot(snapshot);

        mapper.update(locationUpdateDTO, location);
        repository.save(location);

        return mapper.map(location);
    }

    public void delete(Long id) {
        log.info("Deleting location with ID: {}", id);
        repository.deleteById(id);
    }

    public List<LocationCreateDTO> fetchLocations() {
        ResponseEntity<LocationCreateDTO[]> responseEntity = restTemplate.exchange(
                apiUrl, HttpMethod.GET, null, LocationCreateDTO[].class);

        LocationCreateDTO[] locations = responseEntity.getBody();

        return locations != null ? Arrays.asList(locations) : List.of();
    }
}
