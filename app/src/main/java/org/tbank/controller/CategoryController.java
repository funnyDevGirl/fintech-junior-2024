package org.tbank.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.categories.CategoryDTO;
import org.tbank.dto.categories.CategoryUpdateDTO;
import org.tbank.service.CategoryService;
import java.util.List;


@RestController
@RequestMapping("/api/v1/places/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDTO> index() {
        return service.getAll();
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO show(@PathVariable Long id) {

        return service.findById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO create(@RequestBody CategoryCreateDTO categoryCreateDTO) {
        return service.create(categoryCreateDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO update(@RequestBody CategoryUpdateDTO categoryUpdateDTO,
                              @PathVariable Long id) {
        return service.update(categoryUpdateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
