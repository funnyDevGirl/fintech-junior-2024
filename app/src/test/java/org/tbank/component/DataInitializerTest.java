package org.tbank.component;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
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
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;


@ExtendWith(MockitoExtension.class)
public class DataInitializerTest {

    @Mock
    private LocationService locationService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocationRepository locationRepository ;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private Logger logger;

    @InjectMocks
    private DataInitializer dataInitializer;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream(); // Перехват системного вывода
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent)); // Перенаправление системного вывода
        locationService = mock(LocationService.class);
        categoryService = mock(CategoryService.class);
        dataInitializer = new DataInitializer(locationRepository, categoryRepository,
                locationService, categoryService, locationMapper, categoryMapper,
                Executors.newFixedThreadPool(2), Executors.newScheduledThreadPool(2));
    }

    @AfterEach
    public void clean() {
        System.setOut(originalOut); // Возвращаем системный вывод к оригинальному
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testInitLocationsSuccess() throws InterruptedException {
        LocationCreateDTO locationDTO = new LocationCreateDTO("slug", "name");
        Location location = new Location(null, "slug", "name");

        when(locationService.fetchLocations()).thenReturn(List.of(locationDTO));
        when(locationMapper.map(locationDTO)).thenReturn(location);

        // Запускаем многопоточную инициализацию данных
        dataInitializer.run(null);

        // Задержка для ожидания выполнения потоков
        Thread.sleep(500); // Выбор времени ожидания может зависеть от ваших тестов

        verify(locationService).fetchLocations();
        verify(locationMapper).map(locationDTO);
        verify(locationRepository, times(1)).save(location);

        String output = outContent.toString();
        assertTrue(output.contains("Locations initialization completed successfully."));
    }

    @Test
    void testInitCategoriesSuccess() throws InterruptedException {
        CategoryCreateDTO categoryDTO = new CategoryCreateDTO("slug", 200L, "name");
        Category category = new Category(null, 200L, "slug", "name");

        when(categoryService.fetchCategories()).thenReturn(List.of(categoryDTO));
        when(categoryMapper.map(categoryDTO)).thenReturn(category);

        // Запускаем многопоточную инициализацию данных
        dataInitializer.run(null);

        // Задержка для ожидания выполнения потоков
        Thread.sleep(500);

        verify(categoryService).fetchCategories();
        verify(categoryMapper).map(categoryDTO);
        verify(categoryRepository, times(1)).save(category);

        String output = outContent.toString();
        assertTrue(output.contains("Categories initialization completed successfully."));
    }

    @Test
    void testInitLocationsNoData() throws InterruptedException {
        when(locationService.fetchLocations()).thenReturn(Collections.emptyList());

        // Запускаем многопоточную инициализацию данных
        dataInitializer.run(null);

        // Задержка для ожидания выполнения потоков
        Thread.sleep(500);

        verify(locationService).fetchLocations();
        verify(locationRepository, never()).save(any());

        String output = outContent.toString();
        assertTrue(output.contains("No locations found in API response"));
    }

    @Test
    void testInitCategoriesNoData() throws InterruptedException {
        when(categoryService.fetchCategories()).thenReturn(Collections.emptyList());

        // Запускаем многопоточную инициализацию данных
        dataInitializer.run(null);

        // Задержка для ожидания выполнения потоков
        Thread.sleep(500);

        verify(categoryService).fetchCategories();
        verify(categoryRepository, never()).save(any());

        String output = outContent.toString();
        assertTrue(output.contains("No categories found in API response"));
    }

    @Test
    void testRun_ErrorFetchingLocations() throws InterruptedException {
        when(locationService.fetchLocations()).thenThrow(new RuntimeException("Service Error"));

        // Запускаем многопоточную инициализацию данных
        dataInitializer.run(null);

        // Задержка для ожидания выполнения потоков
        Thread.sleep(500);

        String output = outContent.toString();
        assertTrue(output.contains("Error when getting a list of locations"));
    }

    @Test
    void testRun_ErrorFetchingCategories() throws InterruptedException {
        when(categoryService.fetchCategories()).thenThrow(new RuntimeException("Service Error"));

        // Запускаем многопоточную инициализацию данных
        dataInitializer.run(null);

        // Задержка для ожидания выполнения потоков
        Thread.sleep(500);

        String output = outContent.toString();
        assertTrue(output.contains("Error when getting a list of categories"));
    }
}
