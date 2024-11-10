package org.tbank.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.tbank.dto.users.PasswordResetDTO;
import org.tbank.dto.users.UserCreateDTO;
import org.tbank.dto.users.UserDTO;
import org.tbank.dto.users.UserUpdateDTO;
import org.tbank.util.UserUtils;
import org.tbank.service.UserService;

import javax.management.relation.RoleNotFoundException;


@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserUtils userUtils;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO userCreateDTO) throws RoleNotFoundException {
        return userService.create(userCreateDTO);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO get(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isUser(#id)")
    public UserDTO update(@Valid @RequestBody UserUpdateDTO userUpdateDTO, @PathVariable Long id) {
        return userService.updateFullUser(userUpdateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isUser(#id)")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    // Эндпоинт для запроса сброса пароля
    @PostMapping("/password-reset")
    @ResponseStatus(HttpStatus.OK)
    public void requestPasswordReset(@Valid @RequestParam String email) {
        userService.requestPasswordReset(email);
    }

    // Эндпоинт для сброса пароля
    @PutMapping("/password-reset")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@Valid @RequestBody PasswordResetDTO passwordResetDTO) {
        userService.resetAndUpdatePassword(passwordResetDTO);
    }
}
