package org.tbank.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tbank.model.Category;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


class CategoryRepositoryTest {

    private CategoryRepository repository;
    private Category testCategory;

    @BeforeEach
    public void setUp() {
        repository = new CategoryRepository();

        testCategory = new Category(1L, 160L, "testSlug", "testName");
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
        Category category = new Category(2L, 200L, "new-slug", "New name");
        repository.save(category);

        Category result = repository.findById(category.getId()).orElseThrow();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getCityId()).isEqualTo(200L);
        assertThat(result.getSlug()).isEqualTo("new-slug");
    }

    @Test
    void testUpdate() {
        testCategory.setSlug("updated-slug");
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
