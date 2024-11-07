package org.tbank.util;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.tbank.model.User;
import org.tbank.repository.UserRepository;
import static java.lang.String.format;

@Component
@AllArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String email = authentication.getName();

        return userRepository.findByEmail(email).orElseThrow(
                () -> new AuthenticationCredentialsNotFoundException("Not Authorised"));
    }

    public boolean isUser(long id) {
        String userEmail = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(format("User with ID '%s' not found", id)))
                .getEmail();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userEmail.equals(authentication.getName());
    }
}
