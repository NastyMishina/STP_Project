package com.example.is_electroleed.security;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс для хранения информации о пользователе, извлекаемой из JWT токена.
 * Содержит данные о логине и роли пользователя.
 */
@Setter
@Getter
public class TokenInfo {
    private String login;
    private String role;

    /**
     * Конструктор для создания экземпляра TokenInfo.
     *
     * @param login логин пользователя
     * @param role роль пользователя
     */
    public TokenInfo(String login, String role) {
        this.login = login;
        this.role = role;
    }
}
