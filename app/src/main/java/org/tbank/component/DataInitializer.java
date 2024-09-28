package org.tbank.component;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.mapper.CategoryMapper;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Category;
import org.tbank.model.Location;
import org.tbank.repository.CategoryRepository;
import org.tbank.repository.LocationRepository;
import org.tbank.service.CategoryService;
import org.tbank.service.LocationService;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final LocationMapper locationMapper;
    private final CategoryMapper categoryMapper;


    @Override
    public void run(ApplicationArguments args) {

        // init Locations
        logger.info("The initialization of locations started...");

        try {
            List<LocationCreateDTO> locationCreateDTOS = locationService.fetchLocations();

            System.out.println("Количество локаций, полученный от API: " + locationCreateDTOS.size());

            if (!locationCreateDTOS.isEmpty()) {

                logger.info("Received {} locations", locationCreateDTOS.size());

                locationCreateDTOS.forEach(location -> {
                    var loc = locationMapper.map(location);
                    locationRepository.save(loc);
                    logger.info("Saved location: {}", loc);
                });

                logger.info("Locations initialization completed successfully.");

            } else {
                logger.warn("No locations found in API response");
            }

        } catch (Exception e) {
            logger.error("Error when getting a list of locations", e);
        }
        logger.info("Initialization of locations completed.");

        List<Location> locationResult = locationRepository.findAll();

        logger.info("LocationRepository contains {} locations", locationResult.size());


        // init Categories
        logger.info("The initialization of categories started...");

        try {
            List<CategoryCreateDTO> categoryCreateDTOS = categoryService.fetchCategories();

            System.out.println("Количество категорий, полученный от API: " + categoryCreateDTOS.size());

            if (!categoryCreateDTOS.isEmpty()) {

                logger.info("Received {} categories", categoryCreateDTOS.size());

                categoryCreateDTOS.forEach(category -> {
                    var cat = categoryMapper.map(category);
                    categoryRepository.save(cat);
                    logger.info("Saved category: {}", cat);
                });

                logger.info("Categories initialization completed successfully.");

            } else {
                logger.warn("No categories found in API response");
            }

        } catch (Exception e) {
            logger.error("Error when getting a list of categories", e);
        }
        logger.info("Initialization of categories completed.");

        List<Category> categoryResult = categoryRepository.findAll();

        logger.info("CategoryRepository contains {} categories", categoryResult.size());
    }
}
