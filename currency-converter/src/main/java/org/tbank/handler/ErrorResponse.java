package org.tbank.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Error code", example = "404")
    private int code;

    @Schema(description = "Error message", example = "Unsupported currency code")
    private String message;

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
