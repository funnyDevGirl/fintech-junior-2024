package org.tbank.component;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import nl.altindag.log.LogCaptor;
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


    @BeforeEach
    void setUp() {
        locationService = mock(LocationService.class);
        dataInitializer = new DataInitializer(locationRepository, categoryRepository,
                locationService, categoryService, locationMapper, categoryMapper);
    }

    @AfterEach
    public void clean() {
        locationRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testInitLocationsSuccess() {
        LocationCreateDTO locationDTO = new LocationCreateDTO("slug", "name");
        Location location = new Location(null, "slug", "name");

        when(locationService.fetchLocations()).thenReturn(List.of(locationDTO));
        when(locationMapper.map(locationDTO)).thenReturn(location);

        dataInitializer.run(null);


        verify(locationService).fetchLocations();
        verify(locationMapper).map(locationDTO);
        verify(locationRepository).save(location);
    }

    @Test
    void testInitCategoriesSuccess() {
        CategoryCreateDTO categoryDTO = new CategoryCreateDTO("slug", 200L, "name");
        Category category = new Category(null, 200L, "slug", "name");

        when(categoryService.fetchCategories()).thenReturn(List.of(categoryDTO));
        when(categoryMapper.map(categoryDTO)).thenReturn(category);

        dataInitializer.run(null);

        verify(categoryService).fetchCategories();
        verify(categoryMapper).map(categoryDTO);
        verify(categoryRepository).save(category);
    }

    @Test
    void testInitLocationsNoData() {
        when(locationService.fetchLocations()).thenReturn(Collections.emptyList());

        // Перехват системного вывода
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        dataInitializer.run(null);

        verify(locationService).fetchLocations();
        verify(locationRepository, never()).save(any());

        // Проверка лога, если с API запрос не прошел
        String output = outContent.toString();
        assertTrue(output.contains("No locations found in API response"));
    }

    @Test
    void testInitCategoriesNoData() {
        when(categoryService.fetchCategories()).thenReturn(Collections.emptyList());

        // Перехват системного вывода
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        dataInitializer.run(null);

        verify(categoryService).fetchCategories();
        verify(categoryRepository, never()).save(any());

        // Проверка лога, если с API запрос не прошел
        String output = outContent.toString();
        assertTrue(output.contains("No categories found in API response"));
    }
}
