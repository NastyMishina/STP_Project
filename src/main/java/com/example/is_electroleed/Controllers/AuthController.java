package com.example.is_electroleed.Controllers;

import com.example.is_electroleed.entities.User;
import com.example.is_electroleed.repositories.UserRepo;
import com.example.is_electroleed.security.LoginAuthorities;
import com.example.is_electroleed.security.UtilJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для регистрации и авторизации пользователей.
 * Контроллер предоставляет два основных метода:
 * - `/register` — для регистрации нового пользователя.
 * - `/login` — для авторизации пользователя и получения JWT токена.
 * Методы контроллера используют следующие компоненты:
 * - `UserRepo` — репозиторий для работы с пользователями в базе данных.
 * - `UtilJWT` — утилита для генерации и проверки JWT токенов.
 * - `AuthenticationManager` — для аутентификации пользователя.
 * - `PasswordEncoder` — для безопасного хэширования паролей.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UtilJWT jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Обработчик для авторизации пользователя.
     *
     * @param body объект с логином, паролем и ролью для авторизации
     * @return ResponseEntity с JWT токеном и ролью пользователя, или ошибкой
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginHandler(@RequestBody LoginAuthorities body) {
        try {
            // Создание токена для аутентификации
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getLogin(), body.getPassword());

            // Поиск пользователя в базе данных по логину
            User user = userRepo.findByLogin(body.getLogin())
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

            // Аутентификация пользователя
            authManager.authenticate(authInputToken);

            // Генерация JWT токена
            String token = jwtUtil.generateToken(body.getLogin(), body.getRole());

            // Формирование ответа с токеном и ролью
            Map<String, Object> response = new HashMap<>();
            response.put("jwt-token", token);
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);
        } catch (AuthenticationException authExc) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Неверные учетные данные"));
        }
    }
}
