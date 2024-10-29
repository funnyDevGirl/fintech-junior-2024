package org.tbank.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.mapper.CategoryMapper;
import org.tbank.model.Category;
import org.tbank.observers.LoggingObserver;
import org.tbank.repository.CategoryRepository;
import org.tbank.service.CategoryService;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class InitCategoriesCommand implements Command {

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Override
    public void execute() {

        LoggingObserver<Category> categoryObserver = new LoggingObserver<>();
        categoryRepository.addObserver(categoryObserver);

        try {
            List<CategoryCreateDTO> categoryCreateDTOS = categoryService.fetchCategories();

            log.debug("Количество категорий, полученный от API: {}", categoryCreateDTOS.size());

            if (!categoryCreateDTOS.isEmpty()) {

                log.info("Received {} categories", categoryCreateDTOS.size());

                categoryCreateDTOS.forEach(category -> {
                    var cat = categoryMapper.map(category);
                    categoryRepository.save(cat);
                    log.info("Saved category: {}", cat);
                });

                log.info("Categories initialization completed successfully.");

            } else {
                log.warn("No categories found in API response");
            }

        } catch (Exception e) {
            log.error("Error when getting a list of categories", e);
        }
    }
}
