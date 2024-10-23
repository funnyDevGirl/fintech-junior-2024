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
import org.tbank.dto.events.EventCreateDTO;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventFilterDTO;
import org.tbank.mapper.EventMapper;
import org.tbank.model.Event;
import org.tbank.model.Location;
import org.tbank.repository.EventJpaRepository;
import org.tbank.repository.LocationJpaRepository;
import org.tbank.service.EventService;
import org.tbank.util.ModelGenerator;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class EventControllerTest {
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventJpaRepository eventRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private LocationJpaRepository locationRepository;
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
        var request = get("/api/v1/events/{id}", testEvent.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("place_id").isEqualTo(testEvent.getPlace().getId()),
                v -> v.node("name").isEqualTo(testEvent.getName())
        );
    }

    @Test
    public void testShow_NotFound() throws Exception {
        Long nonExistentId = 999L; // несуществующий id

        var request = get("/api/v1/events/{id}", nonExistentId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string("Event with id: " + nonExistentId + " not found")); // Проверка сообщения об ошибке
    }

    @Test
    public void testGetAll() throws Exception {
        var result = mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testSearchByDateRange() throws Exception {
        EventFilterDTO filter = new EventFilterDTO();
        filter.setFromDate(testEvent.getDate());

        mockMvc.perform(post("/api/v1/events/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testSearchByName() throws Exception {
        EventFilterDTO filter = new EventFilterDTO();
        filter.setName(testEvent.getName());

        mockMvc.perform(post("/api/v1/events/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testSearchNonExistentEvents() throws Exception {
        EventFilterDTO filter = new EventFilterDTO();
        filter.setName("NonExistentEvent");

        mockMvc.perform(post("/api/v1/events/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testCreate() throws Exception {
        EventCreateDTO dto = new EventCreateDTO();

        Event event = Instancio.of(modelGenerator.getEventModel()).create();
        dto.setName(event.getName());
        dto.setDate(String.valueOf(event.getDate()));
        dto.setPlaceId(testLocation.getId());

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Event savedEvent = eventRepository.findByName(dto.getName()).orElseThrow();

        assertThat(savedEvent.getName()).isEqualTo(dto.getName());
        assertThat(savedEvent.getPlace().getId()).isEqualTo(dto.getPlaceId());
        assertThat(savedEvent.getDate()).isEqualTo(dto.getDate());
    }

    @Test
    public void testCreateWithNonExistentLocation() throws Exception {
        EventCreateDTO dto = new EventCreateDTO();

        Event event = Instancio.of(modelGenerator.getEventModel()).create();
        dto.setName(event.getName());
        dto.setDate(String.valueOf(event.getDate()));
        dto.setPlaceId(999L);

        var request = post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));


        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateWithNotValidName() throws Exception {
        var dto = eventMapper.map(testEvent);
        dto.setName("");

        var request = post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        Location newLocation = Instancio.of(modelGenerator.getLocationModel()).create();
        locationRepository.save(newLocation);

        EventDTO dto = eventMapper.map(testEvent);

        dto.setPlaceId(newLocation.getId());

        var request = put("/api/v1/events/{id}", testEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var event = eventRepository.findById(dto.getId()).orElseThrow();

        assertThat(event.getName()).isEqualTo(dto.getName());
        assertThat(event.getPlace().getId()).isEqualTo(dto.getPlaceId());
        assertThat(event.getDate()).isEqualTo(dto.getDate());
    }

    @Test
    public void testUpdate_NonExistentPlaceId() throws Exception {
        EventDTO dto = eventMapper.map(testEvent);

        long nonExistentPlaceId = 999L; // несуществующий placeId
        dto.setPlaceId(nonExistentPlaceId);

        var request = put("/api/v1/events/{id}", testEvent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Place with id: " + nonExistentPlaceId + " not found"));
    }

    @Test
    public void testDeleteAnExistingEvent() throws Exception {
        Event event = eventRepository.findById(testEvent.getId()).orElseThrow();
        var request = delete("/api/v1/events/{id}", testEvent.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(eventRepository.existsById(testEvent.getId())).isEqualTo(false);
    }

    @Test
    public void testDeleteNonExistentEvent() throws Exception {
        Long nonExistentId = 999L;

        var request = delete("/api/v1/events/{id}", nonExistentId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound()
                )
                .andExpect(content().string("Event with id: " + nonExistentId + " not found"));

        assertThat(eventRepository.existsById(nonExistentId)).isEqualTo(false);
    }
}
