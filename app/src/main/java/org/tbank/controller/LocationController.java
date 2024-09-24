package org.tbank.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.tbank.annotation.LogExecutionTime;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.dto.locations.LocationDTO;
import org.tbank.dto.locations.LocationUpdateDTO;
import org.tbank.service.LocationService;
import java.util.List;


@LogExecutionTime // логгирую все методы контроллера
@RestController
@RequestMapping("/api/v1/locations")
@AllArgsConstructor
public class LocationController {

    private final LocationService service;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public List<LocationDTO> index() {
        return service.getAll();
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDTO show(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDTO create(@RequestBody LocationCreateDTO locationCreateDTO) {
        return service.create(locationCreateDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDTO update(@RequestBody LocationUpdateDTO locationUpdateDTO,
                              @PathVariable Long id) {
        return service.update(locationUpdateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
