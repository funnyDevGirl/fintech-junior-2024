package org.tbank.component;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@TestPropertySource(properties =
        {"locations-url=http://localhost:${wiremock.server.port}/public-api/v1.4/locations",
        "categories-url=http://localhost:${wiremock.server.port}/public-api/v1.2/place-categories"})
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public class InitializerIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    private static WireMockServer wireMockServer;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeAll
    public static void setUp() {
        postgreSQLContainer.start();

        wireMockServer = new WireMockServer(0); // будет выбран случайный порт
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
        postgreSQLContainer.stop();
    }

    @Test
    void getCategories_shouldReturnCategories() {
        stubFor(get(urlEqualTo("/public-api/v1.2/place-categories"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\": 9, \"slug\": \"homesteads\", \"name\": \"Усадьбы\"}]")));

        String response = restTemplate.getForObject(System.getProperty("categories-url"), String.class);
        assertThat(response).contains("Усадьбы");
    }

    @Test
    void getPlaces_shouldReturnPlaces() {
        stubFor(get(urlEqualTo("/public-api/v1.4/locations"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"slug\": \"msk\", \"name\": \"Москва\"}]")));

        String response = restTemplate.getForObject(System.getProperty("locations-url"), String.class);
        assertThat(response).contains("Москва");
    }
}
