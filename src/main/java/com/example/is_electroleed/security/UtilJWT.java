package com.example.is_electroleed.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Утилитный класс для работы с JWT токенами.
 * Предоставляет методы для генерации и проверки JWT токенов.
 */
@Component
public class UtilJWT {

    @Value("${jwt_secret}")
    private String secret;

    /**
     * Генерация JWT токена для пользователя.
     *
     * @param login логин пользователя
     * @param role роль пользователя
     * @return сгенерированный JWT токен
     * @throws IllegalArgumentException если аргументы некорректны
     * @throws JWTCreationException если ошибка при создании токена
     */
    public String generateToken(String login, String role) throws IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject("User Details")
                .withClaim("login", login)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withIssuer("Electroleed")
                .sign(Algorithm.HMAC256(secret));

    }

    /**
     * Проверка JWT токена и извлечение информации о пользователе.
     *
     * @param token токен для проверки
     * @return TokenInfo объект с информацией о пользователе
     * @throws JWTVerificationException если токен не прошел верификацию
     */
    public TokenInfo verifyToken(String token)throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User Details")
                .withIssuer("Electroleed")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return new TokenInfo(
                jwt.getClaim("login").asString(),
                jwt.getClaim("role").asString()
        );
    }
}
