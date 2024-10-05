package org.tbank.service;

import org.assertj.core.api.AssertionsForClassTypes;
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
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.categories.CategoryDTO;
import org.tbank.dto.categories.CategoryUpdateDTO;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.mapper.CategoryMapper;
import org.tbank.model.Category;
import org.tbank.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Value("${categories-url}")
    private String apiUrl;

    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryMapper mapper;

    @Mock
    private RestTemplate restTemplate;

    private Category testCategory;
    private Long categoryId;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(mapper, repository, restTemplate);

        categoryId = 1L;
        testCategory = new Category(categoryId, 50L, "test-slug", "Test Category");
    }

    @AfterEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    public void testFindById_CategoryFound() {
        // Настройка поведения мока для возврата существующей категории
        when(repository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // Настройка поведения мока для преобразования в DTO
        CategoryDTO categoryDTO = new CategoryDTO(categoryId, 50L, "test-slug", "Test Category");

        when(mapper.map(testCategory)).thenReturn(categoryDTO);

        // Выполняю метод findById
        CategoryDTO result = categoryService.findById(categoryId);

        // Проверка, что результат соответствует ожидаемому
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getName()).isEqualTo("Test Category");

        // Проверка, что метод репозитория был вызван
        verify(repository).findById(categoryId);
        // Проверка, что маппер был вызван
        verify(mapper).map(testCategory);
    }

    @Test
    public void testFindById_CategoryNotFound() {
        // Настройка поведения мока
        when(repository.findById(categoryId)).thenReturn(Optional.empty());

        // Проверка, что исключение выбрасывается
        assertThatThrownBy(() -> categoryService.findById(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category With Id: " + categoryId + " Not Found");

        // Проверка, что метод репозитория был вызван
        verify(repository).findById(categoryId);
    }

    @Test
    public void testGetAll() {
        long id = 2L;
        Category category = new Category(id, 60L, "another-slug", "Another Location");
        List<Category> categories = List.of(testCategory, category);

        CategoryDTO categoryDTO1 = new CategoryDTO(categoryId,50L, "test-slug", "Test Category");
        CategoryDTO categoryDTO2 = new CategoryDTO(id,60L, "another-slug", "Another Category");

        when(repository.findAll()).thenReturn(categories);
        when(mapper.map(testCategory)).thenReturn(categoryDTO1);
        when(mapper.map(category)).thenReturn(categoryDTO2);

        List<CategoryDTO> result = categoryService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(categoryDTO1, categoryDTO2);

        verify(repository).findAll();
        verify(mapper, times(2)).map(any(Category.class));
    }

    @Test
    public void testCreate() {
        CategoryCreateDTO categoryCreateDTO = new CategoryCreateDTO("another-slug", 60L, "Another Category");
        long id = 2L;
        Category category = new Category(id,60L, "another-slug", "Another Category");
        CategoryDTO categoryDTO = new CategoryDTO(id,60L, "another-slug", "Another Category");

        when(mapper.map(categoryCreateDTO)).thenReturn(category);
        when(mapper.map(category)).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.create(categoryCreateDTO);

        assertThat(result).isEqualTo(categoryDTO);

        verify(mapper).map(categoryCreateDTO);
        verify(repository).save(category);
        verify(mapper).map(category);
    }

    @Test
    public void testUpdate_LocationFound() {
        CategoryUpdateDTO categoryUpdateDTO = new CategoryUpdateDTO();
        categoryUpdateDTO.setSlug(JsonNullable.of("updated-slug"));
        Category existingCategory = new Category(categoryId, 50L,"test-slug","Test Category");
        CategoryDTO updatedCategoryDTO = new CategoryDTO(categoryId,50L, "updated-slug", "Test Category");

        when(repository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(mapper.map(existingCategory)).thenReturn(updatedCategoryDTO);

        CategoryDTO result = categoryService.update(categoryUpdateDTO, categoryId);

        assertThat(result).isEqualTo(updatedCategoryDTO);

        verify(repository).findById(categoryId);
        verify(mapper).update(categoryUpdateDTO, existingCategory);
        verify(repository).save(existingCategory);
        verify(mapper).map(existingCategory);
    }

    @Test
    public void testUpdate_LocationNotFound() {
        long id = 2L;
        CategoryUpdateDTO categoryUpdateDTO = new CategoryUpdateDTO();
        categoryUpdateDTO.setSlug(JsonNullable.of("updated-slug"));

        when(repository.findById(id)).thenReturn(Optional.empty());

        AssertionsForClassTypes.assertThatThrownBy(() -> categoryService.update(categoryUpdateDTO, id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category With Id: " + id + " Not Found");

        verify(repository).findById(id);
    }

    @Test
    public void testDelete() {
        categoryService.delete(categoryId);
        verify(repository).deleteById(categoryId);
    }

    @Test
    public void testDeleteById_NonExistentEntity() {
        Long nonExistentId = Long.MAX_VALUE;
        categoryService.delete(nonExistentId);
        verify(repository).deleteById(nonExistentId);
    }

    @Test
    public void testFetchLocations() {
        CategoryCreateDTO[] categoryCreateDTOs = new CategoryCreateDTO[]{
                new CategoryCreateDTO("slug-1", 50L,"Category 1"),
                new CategoryCreateDTO("slug-2", 60L, "Category 2")};
        ResponseEntity<CategoryCreateDTO[]> responseEntity = new ResponseEntity<>(categoryCreateDTOs, HttpStatus.OK);

        when(restTemplate.exchange(apiUrl, HttpMethod.GET, null, CategoryCreateDTO[].class)).thenReturn(responseEntity);

        List<CategoryCreateDTO> result = categoryService.fetchCategories();

        assertThat(result).hasSize(2);
        verify(restTemplate).exchange(apiUrl, HttpMethod.GET, null, CategoryCreateDTO[].class);
    }

    @Test
    public void testFetchLocations_NoBody() {
        ResponseEntity<CategoryCreateDTO[]> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(apiUrl, HttpMethod.GET, null, CategoryCreateDTO[].class)).thenReturn(responseEntity);

        List<CategoryCreateDTO> result = categoryService.fetchCategories();

        assertThat(result).isEmpty();
        verify(restTemplate).exchange(apiUrl, HttpMethod.GET, null, CategoryCreateDTO[].class);
    }
}
