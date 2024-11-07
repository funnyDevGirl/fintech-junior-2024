package org.tbank.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.tbank.config.AppConfig;
import org.tbank.dto.roles.RoleCreateDTO;
import org.tbank.dto.roles.RoleDTO;
import org.tbank.dto.users.PasswordResetDTO;
import org.tbank.dto.users.UserCreateDTO;
import org.tbank.dto.users.UserDTO;
import org.tbank.dto.users.UserUpdateDTO;
import org.tbank.exception.InvalidConfirmationCodeException;
import org.tbank.exception.UserNotFoundException;
import org.tbank.mapper.UserMapper;
import org.tbank.model.Role;
import org.tbank.model.User;
import org.tbank.repository.RoleRepository;
import org.tbank.repository.UserRepository;
import org.tbank.util.UserUtils;
import java.util.Optional;
import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserUtils userUtils;
    private final EmailService emailService;
    private final AppConfig appConfig;


    public UserDTO create(UserCreateDTO userCreateDTO) {
        Role role = roleRepository.findByName(appConfig.getDefaultRoleName())
                .orElse(findOrCreateRole(appConfig.getDefaultRoleName()));

        User user = userMapper.toUser(userCreateDTO);

        user.getRoles().add(role);
        log.debug("User with email '{}' has been assigned the role '{}'",
                user.getEmail(), role);

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    private Role findOrCreateRole(String name) {
        Optional<Role> optionalRole = roleRepository.findByName(name);
        if (optionalRole.isPresent()) {
            log.debug("Role with name: '{}' exists", name);

            return optionalRole.get();
        } else {
            RoleDTO dto = roleService.create(new RoleCreateDTO(name));
            log.debug("Role with name: '{}' has been created", name);

            return roleRepository.findByName(dto.getName()).orElseThrow();
        }
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format("User with ID '%s' not found", id)));

        return userMapper.toDto(user);
    }

    public UserDTO updateFullUser(UserUpdateDTO userUpdateDTO, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format("User with ID '%s' not found", id)));

        userMapper.update(userUpdateDTO, user);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void requestPasswordReset(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user = userOptional.orElseThrow(
                () -> new UsernameNotFoundException(format("The user with the email '%s' was not found.", email)));

        String confirmationCode = generateConfirmationCode();
        String subject = "Password reset code";
        String message = format("Your confirmation code for password reset: %s", confirmationCode);

        emailService.sendEmail(user.getEmail(), subject, message);
        log.debug("An email with a confirmation code has been sent to email '{}'", email);

        saveConfirmationCode(user, confirmationCode);
    }

    private String generateConfirmationCode() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }

    private void saveConfirmationCode(User user, String confirmationCode) {
        user.setConfirmationCode(confirmationCode);
        userRepository.save(user);
        log.debug("ConfirmationCode save successfully");
    }

    public void resetAndUpdatePassword(PasswordResetDTO passwordResetDTO) {
        User user = userUtils.getCurrentUser();
        log.debug("The beginning of password reset and change.");

        if (!user.isConfirmationCodeValid(passwordResetDTO.getConfirmationCode())) {
            throw new InvalidConfirmationCodeException("Invalid confirmation code.");
        }

        userMapper.updatePassword(passwordResetDTO, user);
        userRepository.save(user);

        log.debug("Password reset successfully!");
    }
}
