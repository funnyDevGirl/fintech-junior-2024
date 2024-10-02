package org.tbank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.tbank.dto.locations.LocationDTO;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.mapper.LocationMapper;
import org.tbank.model.Location;
import org.tbank.repository.LocationRepository;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @MockBean
    private LocationRepository repository;

    @MockBean
    private LocationMapper mapper;

    @Test
    public void testFindById_LocationFound() {
        Long locationId = 1L;

        // Тестовый экземпляр локации
        Location location = new Location();
        location.setId(locationId);
        location.setSlug("test-slug");
        location.setName("Test Location");

        // Настройка поведения мока для возврата существующей локации
        when(repository.findById(locationId)).thenReturn(Optional.of(location));

        // Настройка поведения мока для преобразования в DTO
        LocationDTO locationDTO = new LocationDTO(locationId, "test-slug","Test Location");

        when(mapper.map(location)).thenReturn(locationDTO);

        // Выполняю метод findById
        LocationDTO result = locationService.findById(locationId);

        // Проверка, что результат соответствует ожидаемому
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(locationId);
        assertThat(result.getName()).isEqualTo("Test Location");

        // Проверка, что метод репозитория был вызван
        verify(repository).findById(locationId);
        // Проверка, что маппер был вызван
        verify(mapper).map(location);
    }

    @Test
    public void testFindById_LocationNotFound() {
        Long locationId = 1L;

        // Настройка ответа репозитория
        when(repository.findById(locationId)).thenReturn(Optional.empty());

        // Проверка, что исключение выбрасывается
        assertThatThrownBy(() -> locationService.findById(locationId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Location With Id: " + locationId + " Not Found");

        // Проверка вызова метода репозитория
        verify(repository).findById(locationId);
    }
}
