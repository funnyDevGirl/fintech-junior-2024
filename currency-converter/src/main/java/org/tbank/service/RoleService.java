package org.tbank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.tbank.dto.roles.RoleCreateDTO;
import org.tbank.dto.roles.RoleDTO;
import org.tbank.exception.UserNotFoundException;
import org.tbank.mapper.RoleMapper;
import org.tbank.model.Role;
import org.tbank.repository.RoleRepository;
import static java.lang.String.format;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleDTO create(RoleCreateDTO roleCreateDTO) {
        Role role = roleMapper.toRole(roleCreateDTO);
        roleRepository.save(role);

        return roleMapper.toDto(role);
    }

    public RoleDTO findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format("Role with ID '%s' not found", id)));

        return roleMapper.toDto(role);
    }

    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

}
