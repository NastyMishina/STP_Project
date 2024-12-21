package com.example.is_electroleed.repositories;

import com.example.is_electroleed.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User (Пользователь). Этот интерфейс расширяет JpaRepository и
 * предоставляет методы для поиска, проверки наличия пользователя и получения пользователя по логину.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    /**
     * Поиск пользователей по ключевому слову. Метод ищет по всем полям сущности User, включая
     * ID пользователя, логин, пароль и роль.
     *
     * @param keyword Ключевое слово для поиска в различных полях.
     * @return Список пользователей, соответствующих запросу.
     */
    @Query("SELECT u FROM User u WHERE CONCAT(u.IDUser, u.login, u.password, u.role) LIKE %?1%")
    List<User> searchAll(String keyword);

    /**
     * Поиск пользователя по логину. Метод возвращает объект Optional, который может содержать пользователя,
     * если он существует, или быть пустым, если пользователя с таким логином нет.
     *
     * @param login Логин пользователя.
     * @return Optional, содержащий пользователя, если он найден.
     */
    Optional<User> findByLogin(String login);

    /**
     * Проверка существования пользователя с заданным логином. Метод возвращает true, если пользователь с таким логином
     * существует в базе данных, и false в противном случае.
     *
     * @param login Логин пользователя.
     * @return true, если пользователь с таким логином существует, иначе false.
     */
    boolean existsByLogin(String login);
}
