package org.tbank.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.dto.locations.LocationUpdateDTO;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Location;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class LocationRepositoryTest {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private LocationMapper mapper;

    private Location testLocation;


    @BeforeEach
    public void setUp() {
        LocationCreateDTO createDTO = new LocationCreateDTO("testSlug", "testName");
        testLocation = mapper.map(createDTO);

        repository.save(testLocation);
    }

    @AfterEach
    public void clean() {
        repository.deleteAll();
    }


    @Test
    void testGetAll() {
        List<Location> locations = repository.findAll();

        assertThat(locations).hasSize(1);
        assertThat(locations).extracting(Location::getId)
                .contains(testLocation.getId());
        assertThat(locations).extracting(Location::getName)
                .contains("testName");
        assertThat(locations).extracting(Location::getSlug)
                .contains("testSlug");
    }

    @Test
    void testFindById() {
        Location result = repository.findById(testLocation.getId()).orElseThrow();

        assertThat(result.getId()).isEqualTo(testLocation.getId());
        assertThat(result.getName()).isEqualTo("testName");
        assertThat(result.getSlug()).isEqualTo("testSlug");
    }

    @Test
    void testCreate() {
        LocationCreateDTO createDTO = new LocationCreateDTO("new-slug", "New name");
        Location location = mapper.map(createDTO);

        repository.save(location);

        Location result = repository.findById(location.getId()).orElseThrow();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getSlug()).isEqualTo("new-slug");
    }

    @Test
    void testUpdate() {
        LocationUpdateDTO updateDTO = new LocationUpdateDTO();
        updateDTO.setSlug(JsonNullable.of("updated-slug"));

        mapper.update(updateDTO, testLocation);
        repository.save(testLocation);

        Location result = repository.findById(testLocation.getId()).orElseThrow();

        assertThat(result.getId()).isEqualTo(testLocation.getId());
        assertThat(result.getName()).isEqualTo("testName");
        assertThat(result.getSlug()).isEqualTo("updated-slug");
    }

    @Test
    void testDelete() {
        Location result = repository.findById(testLocation.getId()).orElseThrow();
        repository.deleteById(result.getId());

        assertThat(repository.findById(result.getId())).isEmpty();
    }
}
