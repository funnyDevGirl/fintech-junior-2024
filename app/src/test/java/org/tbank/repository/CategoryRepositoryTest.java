package org.tbank.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.categories.CategoryUpdateDTO;
import org.tbank.mapper.CategoryMapper;
import org.tbank.model.Category;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CategoryRepositoryTest {

    private final CategoryRepository repository = new CategoryRepository();

    @Autowired
    private CategoryMapper mapper;

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
    void testGetAll() {
        List<Category> categories = repository.findAll();

        assertThat(categories).hasSize(1);
        assertThat(categories).extracting(Category::getId)
                .contains(testCategory.getId());
        assertThat(categories).extracting(Category::getName)
                .contains("testName");
        assertThat(categories).extracting(Category::getCityId)
                .contains(160L);
        assertThat(categories).extracting(Category::getSlug)
                .contains("testSlug");
    }

    @Test
    void testFindById() {
        Category result = repository.findById(testCategory.getId()).orElseThrow();

        assertThat(result.getId()).isEqualTo(testCategory.getId());
        assertThat(result.getName()).isEqualTo("testName");
        assertThat(result.getSlug()).isEqualTo("testSlug");
    }

    @Test
    void testCreate() {
        CategoryCreateDTO createDTO = new CategoryCreateDTO("new-slug", 200L,"New name");
        Category category = mapper.map(createDTO);

        repository.save(category);

        Category result = repository.findById(category.getId()).orElseThrow();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getCityId()).isEqualTo(200L);
        assertThat(result.getSlug()).isEqualTo("new-slug");
    }

    @Test
    void testUpdate() {
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO();
        updateDTO.setSlug(JsonNullable.of("updated-slug"));

        mapper.update(updateDTO, testCategory);
        repository.save(testCategory);

        Category result = repository.findById(testCategory.getId()).orElseThrow();

        assertThat(result.getId()).isEqualTo(testCategory.getId());
        assertThat(result.getName()).isEqualTo("testName");
        assertThat(result.getCityId()).isEqualTo(160L);
        assertThat(result.getSlug()).isEqualTo("updated-slug");
    }

    @Test
    void testDelete() {
        Category result = repository.findById(testCategory.getId()).orElseThrow();
        repository.deleteById(result.getId());

        assertThat(repository.findById(result.getId())).isEmpty();
    }
}
