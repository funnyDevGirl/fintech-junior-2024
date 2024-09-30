package org.tbank.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.categories.CategoryDTO;
import org.tbank.dto.categories.CategoryUpdateDTO;
import org.tbank.mapper.CategoryMapper;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.repository.CategoryRepository;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {

    @Value("${categories-url}")
    private String apiUrl;

    private final CategoryMapper mapper;
    private final CategoryRepository repository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);


    public List<CategoryDTO> getAll() {
        var categories = repository.findAll();

        return categories.stream().map(mapper::map).toList();
    }

    public CategoryDTO findById(Long id) {
        var category = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category With Id: " + id + " Not Found"));

        return mapper.map(category);
    }

    public CategoryDTO create(CategoryCreateDTO categoryCreateDTO) {
        var category = mapper.map(categoryCreateDTO);
        repository.save(category);

        return mapper.map(category);
    }

    public CategoryDTO update(CategoryUpdateDTO categoryUpdateDTO, Long id) {
        var category = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category With Id: " + id + " Not Found"));

        mapper.update(categoryUpdateDTO, category);
        repository.save(category);

        return mapper.map(category);
    }

    public void delete(Long id) {
        logger.info("Deleting category with ID: {}", id);
        repository.deleteById(id);
    }

    public List<CategoryCreateDTO> fetchCategories() {
        ResponseEntity<CategoryCreateDTO[]> responseEntity = restTemplate.exchange(
                apiUrl, HttpMethod.GET, null, CategoryCreateDTO[].class);

        CategoryCreateDTO[] categories = responseEntity.getBody();

        return categories != null ? Arrays.asList(categories) : List.of();
    }
}
