package org.tbank.component;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.tbank.model.Category;
import org.tbank.model.Location;
import org.tbank.repository.CategoryRepository;
import org.tbank.repository.LocationRepository;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import java.util.Arrays;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public class InitializerIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final LocationRepository locationRepository = new LocationRepository();

    @Container
    public static WireMockContainer wireMockContainer = new WireMockContainer(DockerImageName.parse("wiremock/wiremock:latest"))
            .withMappingFromResource("wiremock/place-categories.json")
            .withMappingFromResource("wiremock/locations.json");


    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        String wireMockUrl = String.format("http://%s:%d",
                wireMockContainer.getHost(),
                wireMockContainer.getMappedPort(8080));
        registry.add("categories-url", () -> wireMockUrl + "/public-api/v1.2/place-categories");
        registry.add("locations-url", () -> wireMockUrl + "/public-api/v1.4/locations");
    }

    @BeforeAll
    public static void setUp() {
        wireMockContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        wireMockContainer.stop();
    }


    @Test
    public void testFetchCategories() {
        String url = String.format("http://%s:%d/public-api/v1.2/place-categories",
                wireMockContainer.getHost(), wireMockContainer.getMappedPort(8080));

        Optional<Category[]> categories = Optional.ofNullable(restTemplate.getForObject(url, Category[].class));

        Arrays.stream(categories.get()).forEach(categoryRepository::save);

        assertThat(categoryRepository.findAll()).hasSize(1);
        assertThat(categoryRepository.findAll()).extracting("name").containsExactlyInAnyOrder("Еда");
    }

    @Test
    public void testFetchLocations() {
        String url = String.format("http://%s:%d/public-api/v1.4/locations",
                wireMockContainer.getHost(), wireMockContainer.getMappedPort(8080));

        Optional<Location[]> locations = Optional.ofNullable(restTemplate.getForObject(url, Location[].class));

        Arrays.stream(locations.get()).forEach(locationRepository::save);

        assertThat(locationRepository.findAll()).hasSize(1);
        assertThat(locationRepository.findAll()).extracting("name").containsExactlyInAnyOrder("Санкт-Петербург");
    }
}
