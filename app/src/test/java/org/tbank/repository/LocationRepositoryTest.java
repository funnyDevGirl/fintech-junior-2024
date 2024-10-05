package org.tbank.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tbank.model.Location;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


class LocationRepositoryTest {

    private LocationRepository repository;
    private Location testLocation;

    @BeforeEach
    public void setUp() {
        repository = new LocationRepository();

        testLocation = new Location(1L, "testSlug", "testName");
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
        Location location = new Location(2L, "new-slug", "New name");
        repository.save(location);

        Location result = repository.findById(location.getId()).orElseThrow();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getSlug()).isEqualTo("new-slug");
    }

    @Test
    void testUpdate() {
        testLocation.setSlug("updated-slug");
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
