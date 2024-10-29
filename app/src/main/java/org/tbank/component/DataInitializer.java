package org.tbank.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.mapper.CategoryMapper;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Category;
import org.tbank.model.Location;
import org.tbank.observers.LoggingObserver;
import org.tbank.repository.CategoryRepository;
import org.tbank.repository.LocationRepository;
import org.tbank.service.CategoryService;
import org.tbank.service.LocationService;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final LocationMapper locationMapper;
    private final CategoryMapper categoryMapper;


    @Override
    public void run(ApplicationArguments args) {

        // init Locations
        LoggingObserver<Location> locationObserver = new LoggingObserver<>();
        locationRepository.addObserver(locationObserver);

        log.info("The initialization of locations started...");

        try {
            List<LocationCreateDTO> locationCreateDTOS = locationService.fetchLocations();

            log.debug("Количество локаций, полученный от API: {}", locationCreateDTOS.size());

            if (!locationCreateDTOS.isEmpty()) {

                log.info("Received {} locations", locationCreateDTOS.size());

                locationCreateDTOS.forEach(location -> {
                    var loc = locationMapper.map(location);
                    locationRepository.save(loc);
                    log.info("Saved location: {}", loc);
                });

                log.info("Locations initialization completed successfully.");

            } else {
                log.warn("No locations found in API response");
            }

        } catch (Exception e) {
            log.error("Error when getting a list of locations", e);
        }
        log.info("Initialization of locations completed.");

        List<Location> locationResult = locationRepository.findAll();

        log.info("LocationRepository contains {} locations", locationResult.size());


        // init Categories
        LoggingObserver<Category> categoryObserver = new LoggingObserver<>();
        categoryRepository.addObserver(categoryObserver);

        log.info("The initialization of categories started...");

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
        log.info("Initialization of categories completed.");

        List<Category> categoryResult = categoryRepository.findAll();

        log.info("CategoryRepository contains {} categories", categoryResult.size());
    }
}
