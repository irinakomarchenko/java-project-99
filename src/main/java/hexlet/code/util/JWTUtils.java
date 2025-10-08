package hexlet.code.util;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for generating JWT tokens.
 */
@Component
public final class JWTUtils {

    private final JwtEncoder encoder;

    public JWTUtils(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Generates a JWT token for the specified username.
     *
     * @param username the username for which the token is generated
     * @return a JWT token as a string
     */
    public String generateToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(361, ChronoUnit.DAYS))
                .subject(username)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
