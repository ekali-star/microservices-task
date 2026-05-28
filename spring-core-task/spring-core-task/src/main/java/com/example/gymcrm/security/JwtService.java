package com.example.gymcrm.security;

import com.example.gymcrm.config.JwtProperties;
import com.example.gymcrm.model.User;
import com.example.gymcrm.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtService {

    private final JwtProperties props;
    private final UserRepository userRepository;

    public JwtService(JwtProperties props, UserRepository userRepository) {
        this.props = props;
        this.userRepository = userRepository;
    }

    public String generateToken(String username) {
        try {
            JWSSigner signer = new MACSigner(props.getSecret().getBytes());

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + props.getExpirationMs()))
                    .jwtID(java.util.UUID.randomUUID().toString())
                    .build();

            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(signer);
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    public JWTClaimsSet validateAndParse(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(props.getSecret().getBytes());
            if (!jwt.verify(verifier)) {
                throw new RuntimeException("Invalid JWT signature");
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (claims.getExpirationTime().before(new Date())) {
                throw new RuntimeException("JWT expired");
            }
            String username = claims.getSubject();
            User user = userRepository.findByUsername(username).orElseThrow();
            if (user.getLastLogout() != null &&
                    claims.getIssueTime().toInstant().isBefore(user.getLastLogout())) {
                throw new RuntimeException("JWT invalidated (logout)");
            }
            return claims;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("JWT validation failed", e);
        }
    }

    public void logout(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setLastLogout(Instant.now());
        userRepository.save(user);
    }
}