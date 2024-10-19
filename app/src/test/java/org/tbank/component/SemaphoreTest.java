package org.tbank.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.service.CategoryService;
import org.tbank.service.LocationService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import static org.mockito.Mockito.*;


public class SemaphoreTest {

    @Mock
    private LocationService locationService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private DataInitializer dataInitializer;

    private final int maxConcurrentRequests = 3;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dataInitializer.setMaxConcurrentRequests(maxConcurrentRequests);
    }

    @Test
    public void testRateLimitingForLocations() throws InterruptedException {
        when(locationService.fetchLocations()).thenReturn(createLocationDTOS(5));

        CountDownLatch latch = new CountDownLatch(5);
        Runnable task = () -> {
            try {
                dataInitializer.fetchLocationsWithRateLimiting();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < 5; i++) {
            new Thread(task).start();
        }

        latch.await(); // Жду завершения всех потоков

        // Проверяю, что ограничение на запросы соблюдено
        verify(locationService, times(5)).fetchLocations();
    }

    @Test
    public void testRateLimitingForCategories() throws InterruptedException {
        when(categoryService.fetchCategories()).thenReturn(createCategoryDTOS(5));

        CountDownLatch latch = new CountDownLatch(5);
        Runnable task = () -> {
            try {
                dataInitializer.fetchCategoriesWithRateLimiting();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < 5; i++) {
            new Thread(task).start();
        }

        latch.await(); // Жду завершения всех потоков

        // Проверяю, что ограничение на запросы соблюдено
        verify(categoryService, times(5)).fetchCategories();
    }

    private List<LocationCreateDTO> createLocationDTOS(int count) {
        List<LocationCreateDTO> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add(new LocationCreateDTO("slug" + i, "name" + i));
        }
        return locations;
    }

    private List<CategoryCreateDTO> createCategoryDTOS(int count) {
        List<CategoryCreateDTO> categories = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            categories.add(new CategoryCreateDTO("slug" + i, (long) i, "name" + i));
        }
        return categories;
    }
}
