package org.tbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.dto.locations.LocationDTO;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Event;
import org.tbank.model.Location;
import org.tbank.repository.EventJpaRepository;
import org.tbank.repository.LocationJpaRepository;
import org.tbank.util.ModelGenerator;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.HashSet;
import java.util.Set;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class LocationControllerTest {
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private LocationJpaRepository locationRepository;
    @Autowired
    private EventJpaRepository eventRepository;
    @Autowired
    private ObjectMapper om;
    private Event testEvent;
    private Location testLocation;

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    static {
        postgresContainer.start();
    }

    @BeforeEach
    public void setUp() {
        // persist
        testLocation = Instancio.of(modelGenerator.getLocationModel()).create();
        locationRepository.save(testLocation);

        testEvent = Instancio.of(modelGenerator.getEventModel()).create();
        testEvent.setPlace(testLocation);
        eventRepository.save(testEvent);

        // merge
        testLocation.addEvent(testEvent);
        locationRepository.save(testLocation);
    }

    @AfterEach
    public void clean() {
        eventRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/v1/locations/{id}", testLocation.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("slug").isEqualTo(testLocation.getSlug()),
                v -> v.node("name").isEqualTo(testLocation.getName())
        );
    }

    @Test
    public void testShow_NotFound() throws Exception {
        Long nonExistentId = 999L; // несуществующий id

        var request = get("/api/v1/locations/{id}", nonExistentId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string("Location with id: " + nonExistentId + " not found"));
    }

    @Test
    public void testGetAll() throws Exception {
        var result = mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        LocationCreateDTO dto = new LocationCreateDTO();

        Location location = Instancio.of(modelGenerator.getLocationModel()).create();
        dto.setName(location.getName());
        dto.setSlug(location.getSlug());

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Location savedLocation = locationRepository.findBySlugWithEvents(dto.getSlug()).orElseThrow();

        assertThat(savedLocation.getName()).isEqualTo(dto.getName());
        assertThat(savedLocation.getSlug()).isEqualTo(dto.getSlug());
    }

    @Test
    public void testCreateWithNotValidName() throws Exception {
        LocationDTO dto = locationMapper.map(testLocation);
        dto.setSlug("");

        var request = post("/api/v1/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        Location newLocation = Instancio.of(modelGenerator.getLocationModel()).create();
        locationRepository.save(newLocation);

        Event newEvent = Instancio.of(modelGenerator.getEventModel()).create();
        newEvent.setPlace(newLocation);
        eventRepository.save(newEvent);

        LocationDTO dto = locationMapper.map(testLocation);

        Set<Long> ids = new HashSet<>();
        ids.add(newEvent.getId());
        dto.setName(newEvent.getName());
        dto.setEventIds(ids);

        var request = put("/api/v1/locations/{id}", testLocation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var event = locationRepository.findByIdWithEvents(dto.getId()).orElseThrow();

        assertThat(event.getName()).isEqualTo(dto.getName());
        assertThat(event.getSlug()).isEqualTo(dto.getSlug());
        assertThat(event.getEvents().size()).isEqualTo(1);
        assertThat(event.getEvents().contains(newEvent));
    }


    @Test
    public void testDeleteAnExistingEvent() throws Exception {
        var request = delete("/api/v1/locations/{id}", testLocation.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(locationRepository.existsById(testLocation.getId())).isEqualTo(false);
    }

    @Test
    public void testDeleteNonExistentEvent() throws Exception {
        Long nonExistentId = 999L;

        var request = delete("/api/v1/locations/{id}", nonExistentId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string("Location with id: " + nonExistentId + " not found"));

        assertThat(locationRepository.existsById(nonExistentId)).isEqualTo(false);
    }
}
