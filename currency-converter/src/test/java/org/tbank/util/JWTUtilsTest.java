package org.tbank.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import static org.mockito.Mockito.*;

public class JWTUtilsTest {

    @Mock
    private JwtEncoder encoder;

    @InjectMocks
    private JWTUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateTokenReturnsValidToken() {
        // Arrange
        JwtEncoder mockEncoder = mock(JwtEncoder.class);
        JWTUtils jwtUtils = new JWTUtils(mockEncoder);
        Jwt mockJwt = mock(Jwt.class);

        when(mockJwt.getTokenValue()).thenReturn("mock.jwt.token");
        when(mockEncoder.encode(any())).thenReturn(mockJwt);

        // Act
        String token = jwtUtils.generateToken("testuser@gmail.com", true);

        // Assert
        Assertions.assertNotNull(token);
        Assertions.assertEquals("mock.jwt.token", token);
    }

    @Test
    public void testGenerateTokenThrowsException() {
        String username = "testuser@gmail.com";
        boolean rememberMe = true;

        // Arrange
        when(encoder.encode(any(JwtEncoderParameters.class))).thenThrow(new RuntimeException("Encoding failed"));

        // Act
        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class, () -> {
            jwtUtils.generateToken(username, rememberMe);
        });

        // Assert
        Assertions.assertEquals("Encoding failed", thrown.getMessage());
    }
}
