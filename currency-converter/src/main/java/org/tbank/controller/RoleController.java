package org.tbank.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tbank.dto.roles.RoleDTO;
import org.tbank.service.RoleService;
import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/api/v1/roles")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleDTO get(@PathVariable Long id) throws RoleNotFoundException {
        return roleService.findById(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }
}
