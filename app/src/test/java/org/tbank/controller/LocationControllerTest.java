package org.tbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Location;
import org.tbank.repository.LocationRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository repository;

    @Autowired
    private ObjectMapper om;

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
    public void testShow() throws Exception {
        var request = get("/api/v1/locations/{id}", testLocation.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testLocation.getName())
        );
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        System.out.println();

        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = new CategoryCreateDTO("slug", 200L,"Name");

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var category = repository.findBySlug(dto.getSlug()).orElseThrow();

        assertThat(category.getName()).isEqualTo(dto.getName());
        assertThat(category.getName()).isNotNull();
    }

    @Test
    public void testUpdate() throws Exception {
        var dto = mapper.map(testLocation);
        dto.setName("Java");

        var request = put("/api/v1/locations/{id}", testLocation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var category = repository.findBySlug(dto.getSlug()).orElseThrow();

        assertThat(category.getName()).isEqualTo("Java");
    }

    @Test
    public void testDelete() throws Exception {
        Long id = testLocation.getId();
        var request = delete("/api/v1/locations/{id}", id);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(repository.findById(id)).isEmpty();
    }
}
