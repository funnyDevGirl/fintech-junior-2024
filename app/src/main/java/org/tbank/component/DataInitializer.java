package org.tbank.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.mapper.CategoryMapper;
import org.tbank.mapper.LocationMapper;
import org.tbank.metrics.InitializationMetric;
import org.tbank.model.Category;
import org.tbank.model.Location;
import org.tbank.repository.CategoryRepository;
import org.tbank.repository.LocationRepository;
import org.tbank.service.CategoryService;
import org.tbank.service.LocationService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final LocationMapper locationMapper;
    private final CategoryMapper categoryMapper;

    @Qualifier("customFixedThreadPool")
    private final ExecutorService fixedThreadPool;

    @Qualifier("customScheduledThreadPool")
    private final ScheduledExecutorService scheduledThreadPool;

    private final List<InitializationMetric> initializationMetrics = new ArrayList<>();

    @Value("${app.dataInitialization.schedule}")
    private Duration scheduleDuration;

    @Value("${app.threadPool.size}")
    private int threadPoolSize;

    public DataInitializer(LocationRepository locationRepository, CategoryRepository categoryRepository,
                           LocationService locationService, CategoryService categoryService,
                           LocationMapper locationMapper, CategoryMapper categoryMapper,
                           @Qualifier("customFixedThreadPool") ExecutorService fixedThreadPool,
                           @Qualifier("customScheduledThreadPool") ScheduledExecutorService scheduledThreadPool) {
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
        this.locationService = locationService;
        this.categoryService = categoryService;
        this.locationMapper = locationMapper;
        this.categoryMapper = categoryMapper;
        this.fixedThreadPool = fixedThreadPool;
        this.scheduledThreadPool = scheduledThreadPool;
    }

    @Override
    public void run(ApplicationArguments args) {
        multithreadedInitializeData();
    }

    @EventListener(ApplicationStartedEvent.class)
    public void scheduleInitOfCategoriesAndLocations() {
        scheduledThreadPool.scheduleAtFixedRate(this::multithreadedInitializeData, 0, scheduleDuration.toMillis(), TimeUnit.MILLISECONDS);
    }


    // можно вызвать в методе run() для отслеживания метрик, вместо multithreadedInitializeData()
    private void initializeDataWithoutMultithreading() {
        long startTime = System.currentTimeMillis(); // Начало замера времени

        initCategories();
        initLocations();

        long endTime = System.currentTimeMillis(); // Конец замера времени
        long duration = endTime - startTime;

        initializationMetrics.add(new InitializationMetric(threadPoolSize, duration));
        log.info("Initializing categories and locations without multithreading took {} ms using {} threads", duration, threadPoolSize);
        printInitializationMetrics();
    }

    private void multithreadedInitializeData() {
        parallelInitOfCategoriesAndLocations(threadPoolSize);
        printInitializationMetrics();
    }

    private void parallelInitOfCategoriesAndLocations(int threadCount) {
        long start = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(2); // 2 задачи

        fixedThreadPool.submit(() -> {
            initLocations();
            latch.countDown();
        });

        fixedThreadPool.submit(() -> {
            initCategories();
            latch.countDown();
        });

        try {
            latch.await(); // Ожидаю завершения обеих задач
        } catch (InterruptedException e) {
            log.error("Initialization of Categories and Locations was interrupted", e);
        }

        long end = System.currentTimeMillis();
        long duration = end - start;

        initializationMetrics.add(new InitializationMetric(threadCount, duration));
        log.info("Initialization of Categories and Locations took {} ms with {} threads", duration, threadCount);
    }

    private void printInitializationMetrics() {
        log.info("Initialization results:");
        log.info("Number of threads | Time (ms)");

        for (InitializationMetric metric : initializationMetrics) {
            log.info("{} | {}", metric.getNumberOfThreads(), metric.getDuration());
        }
    }

    private void initLocations() {
        log.info("The initialization of locations started...");

        try {
            List<LocationCreateDTO> locationCreateDTOS = locationService.fetchLocations();
            log.info("The amount of locations received from the API: {}", locationCreateDTOS.size());

            if (!locationCreateDTOS.isEmpty()) {
                log.info("Started saving {} locations to the database", locationCreateDTOS.size());

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
    }

    private void initCategories() {
        log.info("The initialization of categories started...");

        try {
            List<CategoryCreateDTO> categoryCreateDTOS = categoryService.fetchCategories();
            log.info("The amount of categories received from the API: {}", categoryCreateDTOS.size());

            if (!categoryCreateDTOS.isEmpty()) {
                log.info("Started saving {} categories to the database", categoryCreateDTOS.size());

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
