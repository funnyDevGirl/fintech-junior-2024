package org.tbank.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.tbank.annotation.LogExecutionTime;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.categories.CategoryDTO;
import org.tbank.dto.categories.CategoryUpdateDTO;
import org.tbank.service.CategoryService;
import java.util.List;


@LogExecutionTime // логгирую все методы контроллера
@RestController
@RequestMapping("/api/v1/places/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDTO> getAll() {
        return service.getAll();
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO show(@PathVariable Long id) {

        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO create(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {
        return service.create(categoryCreateDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO update(@Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO,
                              @PathVariable Long id) {
        return service.update(categoryUpdateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
