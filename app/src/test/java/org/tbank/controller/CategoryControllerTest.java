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
import org.tbank.mapper.CategoryMapper;
import org.tbank.model.Category;
import org.tbank.repository.CategoryRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CategoryMapper mapper;

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    static {
        postgresContainer.start();
    }

    private Category testCategory;

    @BeforeEach
    public void setUp() {
        CategoryCreateDTO createDTO = new CategoryCreateDTO("testSlug", 160L, "testName");
        testCategory = mapper.map(createDTO);

        repository.save(testCategory);
    }

    @AfterEach
    public void clean() {
        repository.deleteAll();
    }


    @Test
    public void testShow() throws Exception {
        var request = get("/api/v1/places/categories/{id}", testCategory.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testCategory.getName())
        );
    }

    @Test
    public void testShow_NonExistent() throws Exception {
        mockMvc.perform(get("/api/v1/places/categories/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testGetAll() throws Exception {
        var result = mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        System.out.println();

        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var dto = new CategoryCreateDTO("slug", 200L,"Name");

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var category = repository.findBySlug(dto.getSlug()).orElseThrow();

        assertThat(category.getName()).isEqualTo(dto.getName());
        assertThat(category.getName()).isNotNull();
    }

    @Test
    public void testCreate_InvalidData() throws Exception {
        var dto = new CategoryCreateDTO("", null, null);

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var dto = mapper.map(testCategory);
        dto.setName("Java");

        var request = put("/api/v1/places/categories/{id}", testCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var category = repository.findBySlug(dto.getSlug()).orElseThrow();

        assertThat(category.getName()).isEqualTo("Java");
    }

    @Test
    public void testUpdate_NonExistent() throws Exception {
        var dto = mapper.map(testCategory);
        dto.setName("Updated Name");

        mockMvc.perform(put("/api/v1/locations/{id}", Long.MAX_VALUE) // Не существующий id
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDelete() throws Exception {
        Long id = testCategory.getId();
        var request = delete("/api/v1/places/categories/{id}", id);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(repository.findById(id)).isEmpty();
    }
}
