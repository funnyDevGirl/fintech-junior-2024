package org.tbank.util;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@AllArgsConstructor
public class JWTUtils {

    private final JwtEncoder encoder;

    public String generateToken(String username, boolean rememberMe) {
            Instant now = Instant.now();
            long expirationTime = rememberMe ? 30 : 10;
            Instant expiration = now.plus(expirationTime, rememberMe ? ChronoUnit.DAYS : ChronoUnit.MINUTES);

            JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiration)
                .subject(username)
                .build();

            return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }
}
