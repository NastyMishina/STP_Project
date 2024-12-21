package com.example.is_electroleed.security;

import com.example.is_electroleed.entities.User;
import com.example.is_electroleed.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сервис, реализующий интерфейс UserDetailsService, который используется для загрузки данных о пользователе по его login.
 * Этот сервис извлекает информацию о пользователе из базы данных, используя репозиторий UserRepo,
 * и преобразует её в объект, соответствующий Spring Security (UserDetails).
 * Сервис используется для авторизации пользователей в системе.
 */
@Component
public class CompanyUserDetailsService implements UserDetailsService {

    @Autowired private UserRepo userRepo;

    /**
     * Загружает данные пользователя по его логину. Метод проверяет, существует ли пользователь с данным логином в базе данных.
     * Если пользователь не найден, выбрасывается исключение UsernameNotFoundException.
     * После нахождения пользователя создается объект SimpleGrantedAuthority для роли пользователя,
     * который добавляется в коллекцию authorities для использования в Spring Security.
     *
     * @param login Логин пользователя, данные о котором требуется загрузить.
     * @return Объект UserDetails, содержащий логин, пароль и роль пользователя.
     * @throws UsernameNotFoundException Если пользователь с данным логином не найден в базе данных.
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> userRes = userRepo.findByLogin(login);

        if (userRes.isEmpty())
            throw new UsernameNotFoundException("Невозможно найти пользователя с login: " + login);
        User user = userRes.get();
        String role = String.valueOf(user.getRole());

        // Создание списка authorities для роли пользователя
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role));

            return new org.springframework.security.core.userdetails.User(
                    login,
                    user.getPassword(),
                    authorities);
    }
}
