package org.tbank.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.tbank.command.Command;
import org.tbank.command.InitCategoriesCommand;
import org.tbank.command.InitLocationsCommand;
import org.tbank.mapper.CategoryMapper;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Category;
import org.tbank.model.Location;
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

        Command initLocations = new InitLocationsCommand(locationRepository, locationService, locationMapper);
        Command initCategories = new InitCategoriesCommand(categoryRepository, categoryService, categoryMapper);

        // init Locations
        log.info("The initialization of locations started...");

        initLocations.execute();

        log.info("Initialization of locations completed.");

        List<Location> locationResult = locationRepository.findAll();
        log.info("LocationRepository contains {} locations", locationResult.size());


        // init Categories
        log.info("The initialization of categories started...");

        initCategories.execute();

        log.info("Initialization of categories completed.");

        List<Category> categoryResult = categoryRepository.findAll();
        log.info("CategoryRepository contains {} categories", categoryResult.size());
    }
}
