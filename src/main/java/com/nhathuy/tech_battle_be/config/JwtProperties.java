package com.nhathuy.tech_battle_be.config;

import jakarta.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    @Value("${app.jwt.access-token-expiration:900}")
    private long accessTokenExpirySeconds;

    @Value("${app.jwt.refresh-token-expiration:604800}")
    private long refreshTokenExpirySeconds;

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    // Generated fresh on every startup: acceptable for MVP, but means access/refresh
    // tokens issued before a restart stop validating. Externalize to a persisted PEM
    // keypair via config before running multiple instances or needing restart-safe sessions.
    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            this.publicKey = (RSAPublicKey) keyPair.getPublic();
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("RSA algorithm is unavailable", exception);
        }
    }
}
