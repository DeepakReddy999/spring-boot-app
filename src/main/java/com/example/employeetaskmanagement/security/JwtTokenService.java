package com.example.employeetaskmanagement.security;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.employeetaskmanagement.dto.TokenResponseDto;
import com.example.employeetaskmanagement.entity.User;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;
    private final String issuer;

    public JwtTokenService(
        JwtEncoder jwtEncoder,
        @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes,
        @Value("${security.jwt.issuer:customer-management-system}") String issuer
    ) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
        this.issuer = issuer;
    }

    public TokenResponseDto generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationMinutes * 60);
        List<String> roles = List.of(user.getRole().name());

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(user.getEmail())
            .claim("roles", roles)
            .claim("name", user.getFullName())
            .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

        TokenResponseDto response = new TokenResponseDto();
        response.setAccessToken(tokenValue);
        response.setTokenType("Bearer");
        response.setExpiresAt(expiresAt);
        response.setUsername(user.getEmail());
        response.setRoles(roles);
        return response;
    }
}
