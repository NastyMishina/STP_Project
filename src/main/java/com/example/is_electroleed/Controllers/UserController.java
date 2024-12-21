package com.example.is_electroleed.Controllers;

import com.example.is_electroleed.entities.User;
import com.example.is_electroleed.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для получения информации о текущем пользователе.
 * Контроллер предоставляет один метод:
 * - `/info` — для получения информации о пользователе, который в данный момент авторизован.
 * Методы контроллера используют следующий компонент:
 * - `UserRepo` — репозиторий для работы с данными пользователей в базе данных.
 */
@RestController
@RequestMapping("/userPage")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    /**
     * Получение информации о текущем пользователе.
     * Этот метод получает логин авторизованного пользователя,
     * используя данные из контекста безопасности, и возвращает объект пользователя.
     *
     * @return User объект, содержащий информацию о текущем пользователе
     */
    @GetMapping("/info")
    public User getUserDetails(){
        String login = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByLogin(login).get();
    }
}
