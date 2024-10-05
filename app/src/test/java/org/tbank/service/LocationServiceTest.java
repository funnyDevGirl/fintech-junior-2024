package org.tbank.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.dto.locations.LocationDTO;
import org.tbank.dto.locations.LocationUpdateDTO;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Location;
import org.tbank.repository.LocationRepository;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Value("${locations-url}")
    private String apiUrl;

    @Mock
    private LocationRepository repository;

    @Mock
    private LocationMapper mapper;

    @Mock
    private RestTemplate restTemplate;

    private Location testLocation;
    private Long locationId;
    private LocationService locationService;


    @BeforeEach
    void setUp() {
        locationService = new LocationService(mapper, repository, restTemplate);

        locationId = 1L;
        testLocation = new Location(locationId, "test-slug", "Test Location");
    }

    @AfterEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    public void testFindById_LocationFound() {
        // Настройка поведения мока для возврата существующей локации
        when(repository.findById(locationId)).thenReturn(Optional.of(testLocation));

        // Настройка поведения мока для преобразования в DTO
        LocationDTO locationDTO = new LocationDTO(locationId, "test-slug","Test Location");

        when(mapper.map(testLocation)).thenReturn(locationDTO);

        // Выполняю метод findById
        LocationDTO result = locationService.findById(locationId);

        // Проверка, что результат соответствует ожидаемому
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(locationId);
        assertThat(result.getName()).isEqualTo("Test Location");

        // Проверка, что метод репозитория был вызван
        verify(repository).findById(locationId);
        // Проверка, что маппер был вызван
        verify(mapper).map(testLocation);
    }

    @Test
    public void testFindById_LocationNotFound() {
        // Настройка ответа репозитория
        when(repository.findById(locationId)).thenReturn(Optional.empty());

        // Проверка, что исключение выбрасывается
        assertThatThrownBy(() -> locationService.findById(locationId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Location With Id: " + locationId + " Not Found");

        // Проверка вызова метода репозитория
        verify(repository).findById(locationId);
    }

    @Test
    public void testGetAll() {
        long id = 2L;
        Location location = new Location(id, "another-slug", "Another Location");
        List<Location> locations = List.of(testLocation, location);

        LocationDTO locationDTO1 = new LocationDTO(locationId,"test-slug", "Test Location");
        LocationDTO locationDTO2 = new LocationDTO(id,"another-slug", "Another Location");

        when(repository.findAll()).thenReturn(locations);
        when(mapper.map(testLocation)).thenReturn(locationDTO1);
        when(mapper.map(location)).thenReturn(locationDTO2);

        List<LocationDTO> result = locationService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(locationDTO1, locationDTO2);

        verify(repository).findAll();
        verify(mapper, times(2)).map(any(Location.class));
    }

    @Test
    public void testCreate() {
        LocationCreateDTO locationCreateDTO = new LocationCreateDTO("another-slug", "Another Location");
        long id = 2L;
        Location location = new Location(id,"another-slug", "Another Location");
        LocationDTO locationDTO = new LocationDTO(id,"another-slug", "Another Location");

        when(mapper.map(locationCreateDTO)).thenReturn(location);
        when(mapper.map(location)).thenReturn(locationDTO);

        LocationDTO result = locationService.create(locationCreateDTO);

        assertThat(result).isEqualTo(locationDTO);

        verify(mapper).map(locationCreateDTO);
        verify(repository).save(location);
        verify(mapper).map(location);
    }

    @Test
    public void testUpdate_LocationFound() {
        LocationUpdateDTO locationUpdateDTO = new LocationUpdateDTO();
        locationUpdateDTO.setSlug(JsonNullable.of("updated-slug"));
        Location existingLocation = new Location(locationId,"test-slug", "Test Location");
        LocationDTO updatedLocationDTO = new LocationDTO(locationId,"updated-slug", "Test Location");

        when(repository.findById(locationId)).thenReturn(Optional.of(existingLocation));
        when(mapper.map(existingLocation)).thenReturn(updatedLocationDTO);

        LocationDTO result = locationService.update(locationUpdateDTO, locationId);

        assertThat(result).isEqualTo(updatedLocationDTO);

        verify(repository).findById(locationId);
        verify(mapper).update(locationUpdateDTO, existingLocation);
        verify(repository).save(existingLocation);
        verify(mapper).map(existingLocation);
    }

    @Test
    public void testUpdate_LocationNotFound() {
        long id = 2L;
        LocationUpdateDTO locationUpdateDTO = new LocationUpdateDTO();
        locationUpdateDTO.setSlug(JsonNullable.of("updated-slug"));

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.update(locationUpdateDTO, id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Location With Id: " + id + " Not Found");

        verify(repository).findById(id);
    }

    @Test
    public void testDelete() {
        locationService.delete(locationId);
        verify(repository).deleteById(locationId);
    }

    @Test
    public void testDeleteById_NonExistentEntity() {
        Long nonExistentId = Long.MAX_VALUE;
        locationService.delete(nonExistentId);
        verify(repository).deleteById(nonExistentId);
    }

    @Test
    public void testFetchLocations() {
        LocationCreateDTO[] locationCreateDTOs = new LocationCreateDTO[]{
                new LocationCreateDTO("slug-1", "Location 1"),
                new LocationCreateDTO("slug-2", "Location 2")};
        ResponseEntity<LocationCreateDTO[]> responseEntity = new ResponseEntity<>(locationCreateDTOs, HttpStatus.OK);

        when(restTemplate.exchange(apiUrl, HttpMethod.GET, null, LocationCreateDTO[].class)).thenReturn(responseEntity);

        List<LocationCreateDTO> result = locationService.fetchLocations();

        assertThat(result).hasSize(2);
        verify(restTemplate).exchange(apiUrl, HttpMethod.GET, null, LocationCreateDTO[].class);
    }

    @Test
    public void testFetchLocations_NoBody() {
        ResponseEntity<LocationCreateDTO[]> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(apiUrl, HttpMethod.GET, null, LocationCreateDTO[].class)).thenReturn(responseEntity);

        List<LocationCreateDTO> result = locationService.fetchLocations();

        assertThat(result).isEmpty();
        verify(restTemplate).exchange(apiUrl, HttpMethod.GET, null, LocationCreateDTO[].class);
    }
}
