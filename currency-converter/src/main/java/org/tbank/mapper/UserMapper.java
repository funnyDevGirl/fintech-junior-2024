package org.tbank.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.tbank.dto.users.PasswordResetDTO;
import org.tbank.dto.users.UserCreateDTO;
import org.tbank.dto.users.UserDTO;
import org.tbank.dto.users.UserUpdateDTO;
import org.tbank.model.Role;
import org.tbank.model.User;
import org.tbank.repository.RoleRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeMapping
    public void encryptPassword(UserCreateDTO userCreateDTO) {
        var password = userCreateDTO.getPassword();
        userCreateDTO.setPassword(passwordEncoder.encode(password));
    }

    @BeforeMapping
    public void encryptPasswordUpdate(UserUpdateDTO userUpdateDTO, @MappingTarget User user) {
        var password = userUpdateDTO.getPassword();
        if (password != null && password.isPresent()) {
            user.setPasswordDigest(passwordEncoder.encode(password.get()));
        }
    }

    @BeforeMapping
    public void encryptPasswordReset(PasswordResetDTO passwordResetDTO) {
        var password = passwordResetDTO.getNewPassword();
        passwordResetDTO.setNewPassword(passwordEncoder.encode(password));
    }

    @Mapping(source = "password", target = "passwordDigest")
    @Mapping(source = "roleNames", target = "roles", qualifiedByName = "roleNamesToRoles")
    public abstract User toUser(UserCreateDTO userCreateDTO);

    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "rolesToRoleNames")
    public abstract UserDTO toDto(User user);

    @Mapping(source = "password", target = "passwordDigest")
    @Mapping(source = "roleNames", target = "roles", qualifiedByName = "roleNamesToRoles")
    public abstract void update(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

    @Mapping(source = "newPassword", target = "passwordDigest")
    public abstract void updatePassword(PasswordResetDTO passwordResetDTO, @MappingTarget User user);

    @Named("roleNamesToRoles")
    public Set<Role> roleNamesToRoles(Set<String> roleNames) {
        return roleNames == null ? new HashSet<>()
                : roleRepository.findByNameIn(roleNames);
    }

    @Named("rolesToRoleNames")
    public Set<String> rolesToRoleNames(Set<Role> roles) {
        return roles == null ? new HashSet<>()
                : roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
