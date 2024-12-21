package com.example.is_electroleed.service;

import com.example.is_electroleed.entities.User;
import com.example.is_electroleed.exceptions.DatabaseException;
import com.example.is_electroleed.exceptions.DuplicateEntityException;
import com.example.is_electroleed.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями.
 * <p>
 * Этот класс предоставляет методы для создания, удаления и обновления пользователей
 * в базе данных.
 * </p>
 */
@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Проверяет наличие сотрудника по ID.
     *
     * @param login - логин сотрудника
     * @throws EntityNotFoundException, если сотрудник не найден.
     */
    public Optional<User> checkPresence(String login) {
        Optional<User> userOptional = userRepo.findByLogin(login);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("Пользователь с login " + login + " не найден");
        }
        return userOptional;
    }

    /**
     * Проверяет наличие пользователя по ключевому слову.
     *
     * @param keyword - слово для поиска по таблицы
     */
    public List<User> getAllUsers(String keyword) {
        if (keyword != null){
            return userRepo.searchAll(keyword);
        }
        return userRepo.findAll();
    }

    /**
     * Находит пользователя по login.
     *
     * @param login - идентификатор пользователя
     * @throws EntityNotFoundException, если пользователь не найден.
     */
    public User findByLogin(String login) {
        return userRepo.findByLogin(login).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с заданным login не найден " + login));
    }

    /**
     * Находит пользователя по ID.
     *
     * @param id - идентификатор сотрудника
     * @throws EntityNotFoundException, если сотрудник не найден.
     */
    public User findById(Integer id) {
        return userRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с заданным Id не найден " + id));
    }

    /**
     * Сохраняет объект пользователя в базе данных.
     *
     * @param user объект пользователя для сохранения
     * @throws IllegalArgumentException если объект пользователя невалиден
     * @throws DuplicateEntityException если пользователь с таким идентификатором уже существует (лучше логином)
     * @throws DatabaseException        если произошла ошибка на уровне базы данных
     */
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Объект сотрудника не может быть null.");
        }
        if (user.getLogin() != null && userRepo.existsByLogin(user.getLogin())) {
            throw new DuplicateEntityException("Сотрудник с логином " + user.getLogin() + " уже существует.");
        }
        try {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
            userRepo.save(user);
        } catch (DatabaseException ex) {
            throw new DatabaseException("Ошибка при сохранении сотрудника", ex);
        }
    }

    public void edit(User user) {
        if (user.getID() != null) {
            User existingUser = userRepo.findById(user.getID())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
            existingUser.setLogin(user.getLogin());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            existingUser.setRole(user.getRole());
            userRepo.save(existingUser);
        } else {
            if (userRepo.findByLogin(user.getLogin()).isPresent()) {
                throw new IllegalArgumentException("Пользователь с таким логином уже существует");
            }
            userRepo.save(user);
        }
    }

    /**
     * Удаляет пользователя по login.
     *
     * @param login - идентификатор пользователя
     * @throws EntityNotFoundException, если пользователь не найден.
     * @throws DatabaseException, если возникли проблемы с базой данных.
     */
    public void deleteByLogin(String login) {
        try {
            Optional<User> userOptional =  checkPresence(login);
            User user = userOptional.get();
            userRepo.delete(user);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Ошибка при удалении сотрудника", ex);
        }
    }

    /**
     * Возвращает список всех пользователей, отсортированных по полю login в порядке убывания.
     *
     * @return список пользователей, отсортированных по полю login в порядке убывания.
     */
    public List<User> findAllSortedByLoginDesc() {
        return userRepo.findAll(Sort.by(Sort.Order.desc("login")));
    }

    /**
     * Возвращает список всех пользователей, отсортированных по полю login в порядке возрастания.
     *
     * @return список пользователей, отсортированных по полю login в порядке возрастания.
     */
    public List<User> findAllSortedByLoginAsc() {
        return userRepo.findAll(Sort.by(Sort.Order.asc("login")));
    }

    /**
     * Возвращает список всех пользователей, отсортированных по полю role в порядке убывания.
     *
     * @return список пользователей, отсортированных по полю role в порядке убывания.
     */
    public List<User> findAllSortedByRoleDesc() {
        return userRepo.findAll(Sort.by(Sort.Order.desc("role")));
    }

    /**
     * Возвращает список всех пользователей, отсортированных по полю role в порядке возрастания.
     *
     * @return список пользователей, отсортированных по полю role в порядке возрастания.
     */
    public List<User> findAllSortedByRoleAsc() {
        return userRepo.findAll(Sort.by(Sort.Order.asc("role")));
    }
}
