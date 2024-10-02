package org.tbank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.tbank.dto.categories.CategoryDTO;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.mapper.CategoryMapper;
import org.tbank.model.Category;
import org.tbank.repository.CategoryRepository;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository repository;

    @MockBean
    private CategoryMapper mapper;

    @Test
    public void testFindById_CategoryFound() {
        Long categoryId = 1L;

        // Тестовый экземпляр категории
        Category category = new Category();
        category.setId(categoryId);
        category.setSlug("test-slug");
        category.setName("Test Category");

        // Настройка поведения мока для возврата существующей категории
        when(repository.findById(categoryId)).thenReturn(Optional.of(category));

        // Настройка поведения мока для преобразования в DTO
        CategoryDTO categoryDTO = new CategoryDTO(categoryId, 50L, "test-slug", "Test Category");

        when(mapper.map(category)).thenReturn(categoryDTO);

        // Выполняю метод findById
        CategoryDTO result = categoryService.findById(categoryId);

        // Проверка, что результат соответствует ожидаемому
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getName()).isEqualTo("Test Category");

        // Проверка, что метод репозитория был вызван
        verify(repository).findById(categoryId);
        // Проверка, что маппер был вызван
        verify(mapper).map(category);
    }

    @Test
    public void testFindById_CategoryNotFound() {
        Long categoryId = 1L;

        // Настройка поведения мока
        when(repository.findById(categoryId)).thenReturn(Optional.empty());

        // Проверка, что исключение выбрасывается
        assertThatThrownBy(() -> categoryService.findById(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category With Id: " + categoryId + " Not Found");

        // Проверка, что метод репозитория был вызван
        verify(repository).findById(categoryId);
    }
}
