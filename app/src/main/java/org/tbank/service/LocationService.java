package org.tbank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.tbank.repository.LocationRepository;
import java.util.Arrays;
import java.util.List;


@Service
public class LocationService {

    @Value("${locations-url}")
    private String apiUrl;

    private final LocationMapper mapper;
    private final LocationRepository repository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    public LocationService(LocationRepository repository,
                           LocationMapper mapper,
                           RestTemplate restTemplate) {
        this.repository = repository;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }


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

        mapper.update(locationUpdateDTO, location);
        repository.save(location);

        return mapper.map(location);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<LocationCreateDTO> fetchLocations() {
        ResponseEntity<LocationCreateDTO[]> responseEntity = restTemplate.exchange(
                apiUrl, HttpMethod.GET, null, LocationCreateDTO[].class);

        LocationCreateDTO[] locations = responseEntity.getBody();

        return locations != null ? Arrays.asList(locations) : List.of();
    }
}
