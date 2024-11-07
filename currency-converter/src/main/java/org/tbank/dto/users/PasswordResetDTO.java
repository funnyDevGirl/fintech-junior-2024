package org.tbank.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO {
    @NotBlank
    @Size(min = 3)
    private String newPassword;

    @NotBlank
    private String confirmationCode;
}
