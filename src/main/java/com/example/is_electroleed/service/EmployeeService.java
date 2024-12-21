package com.example.is_electroleed.service;

import com.example.is_electroleed.entities.DTO.EmployeeDTO;
import com.example.is_electroleed.entities.Employee;
import com.example.is_electroleed.entities.User;
import com.example.is_electroleed.exceptions.DatabaseException;
import com.example.is_electroleed.exceptions.DuplicateEntityException;
import com.example.is_electroleed.repositories.EmployeeRepo;
import com.example.is_electroleed.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления сотрудниками.
 * Этот класс предоставляет методы для создания, удаления и обновления сотрудников
 * в базе данных.
 *
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private UserRepo userRepo;

    /**
     * Проверяет наличие сотрудника по ID.
     *
     * @param id - идентификатор сотрудника
     * @throws EntityNotFoundException, если сотрудник не найден.
     */
    public Optional<Employee> checkPresence(int id) {
        Optional<Employee> employeeOptional = employeeRepo.findById(id);
        if (employeeOptional.isEmpty()) {
            throw new EntityNotFoundException("Сотрудник с ID " + id + " не найден");
        }
        return employeeOptional;
    }

    /**
     * Проверяет наличие сотрудника по ключевому слову.
     *
     * @param keyword - слово для поиска по таблицы
     */
    public  List<Employee> getAllEmployees(String keyword) {
        if (keyword != null){
            return employeeRepo.searchAll(keyword);
        }
        return employeeRepo.findAll();
    }

    public  List<EmployeeDTO> getAllEmployeesDTO(String keyword) {
        if (keyword != null){
            return employeeRepo.searchAllWithUsernames(keyword);
        }
        return employeeRepo.searchAllDTO();
    }

    /**
     * Находит сотрудника по ID.
     *
     * @param id - идентификатор сотрудника
     * @throws EntityNotFoundException, если сотрудник не найден.
     */
    public Employee findById(Integer id) {
        return employeeRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Сотрудник с заданным Id не найден " + id));
    }

    /**
     * Сохраняет объект сотрудника в базе данных.
     *
     * @param employee объект сотрудника для сохранения
     * @throws IllegalArgumentException если объект сотрудника невалиден
     * @throws DuplicateEntityException если сотрудник с таким идентификатором уже существует
     * @throws DatabaseException        если произошла ошибка на уровне базы данных
     */
    public void save(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Объект сотрудника не может быть null.");
        }

        if (employee.getID() != null && employeeRepo.existsById(employee.getID())) {
            throw new DuplicateEntityException("Сотрудник с ID " + employee.getID() + " уже существует.");
        }
        try {
            User user = userRepo.findById(employee.getAccount().getID())
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким ID не найден"));

            employee.setAccount(user);

            employeeRepo.save(employee);
        } catch (DatabaseException ex) {
            throw new DatabaseException("Ошибка при сохранении сотрудника", ex);
        }
    }


    public void edit(Employee employee) {
        if (employee.getID() != null) {
            Employee existingEmpl = employeeRepo.findById(employee.getID())
                    .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));

            existingEmpl.setFullName(employee.getFullName());
            existingEmpl.setPosition(employee.getPosition());

            if (employee.getAccount() != null) {
                User account = userRepo.findById(employee.getAccount().getID())
                        .orElseThrow(() -> new IllegalArgumentException("Аккаунт не найден"));
                existingEmpl.setAccount(account);
            }

            employeeRepo.save(existingEmpl);
        }
    }

    /**
     * Удаляет сотрудника по ID.
     *
     * @param id - идентификатор сотрудника
     * @throws EntityNotFoundException, если сотрудник не найден.
     * @throws IllegalStateException, если удаление невозможно из-за бизнес-логики.
     */
    public void deleteById(int id) {
        Optional<Employee> employeeOptional =  checkPresence(id);
        Employee employee = employeeOptional.get();

        if (!employee.getProjects().isEmpty()) {
            throw new IllegalStateException("Сотрудник участвует в активных проектах.");
        }
        try {
            employeeRepo.delete(employee);
        } catch (DataAccessException ex) {
            throw new DatabaseException("Ошибка при удалении сотрудника", ex);
        }
    }

    /**
     * Возвращает список сотрудников, отсортированных по имени (fullName) в порядке убывания.
     * Используется для получения списка сотрудников, упорядоченного по алфавиту в обратном порядке.
     *
     * @return список сотрудников, отсортированных по полю fullName в порядке убывания.
     */
    public List<Employee> findAllSortedByFullNameDesc() {
        return employeeRepo.findAll(Sort.by(Sort.Order.desc("fullName")));
    }

    /**
     * Возвращает список сотрудников, отсортированных по имени (fullName) в порядке возрастания.
     * Используется для получения списка сотрудников, упорядоченного по алфавиту в порядке возрастания.
     *
     * @return список сотрудников, отсортированных по полю fullName в порядке возрастания.
     */
    public List<Employee> findAllSortedByFullNameAsc() {
        return employeeRepo.findAll(Sort.by(Sort.Order.asc("fullName")));
    }

    /**
     * Возвращает список сотрудников, отсортированных по должности (position) в порядке убывания.
     * Это полезно, если нужно отсортировать сотрудников с высшими должностями в начале списка.
     *
     * @return список сотрудников, отсортированных по полю position в порядке убывания.
     */
    public List<Employee> findAllSortedByPositionDesc() {
        return employeeRepo.findAll(Sort.by(Sort.Order.desc("position")));
    }

    /**
     * Возвращает список сотрудников, отсортированных по должности (position) в порядке возрастания.
     * Это полезно для сортировки сотрудников по их должностям от младших к старшим.
     *
     * @return список сотрудников, отсортированных по полю position в порядке возрастания.
     */
    public List<Employee> findAllSortedByPositionAsc() {
        return employeeRepo.findAll(Sort.by(Sort.Order.asc("position")));
    }

    /**
     * Возвращает список объектов EmployeeDTO, отсортированных по должности (position) в порядке убывания.
     * DTO может включать только те данные, которые необходимы для отображения на клиенте.
     *
     * @return список объектов DTO сотрудников, отсортированных по полю position в порядке убывания.
     */
    public List<EmployeeDTO> findAllSortedDTOByPositionDesc() {
        return employeeRepo.findAllSortedDTOByPositionDesc();
    }

    /**
     * Возвращает список объектов EmployeeDTO, отсортированных по должности (position) в порядке возрастания.
     * DTO используется для отображения данных сотрудников на клиенте.
     *
     * @return список объектов DTO сотрудников, отсортированных по полю position в порядке возрастания.
     */
    public List<EmployeeDTO> findAllSortedDTOByPositionAsc() {
        return employeeRepo.findAllSortedDTOByPositionAsc();
    }

    /**
     * Возвращает список объектов EmployeeDTO, отсортированных по логину (login) в порядке убывания.
     * Это полезно, если нужно показать пользователей с наибольшими логинами в начале списка.
     *
     * @return список объектов DTO сотрудников, отсортированных по полю login в порядке убывания.
     */
    public List<EmployeeDTO> findAllSortedDTOByLoginDesc() {
        return employeeRepo.findAllSortedDTOByLoginDesc();
    }

    /**
     * Возвращает список объектов EmployeeDTO, отсортированных по логину (login) в порядке возрастания.
     * Это полезно для отображения сотрудников в алфавитном порядке по их логинам.
     *
     * @return список объектов DTO сотрудников, отсортированных по полю login в порядке возрастания.
     */
    public List<EmployeeDTO> findAllSortedDTOByLoginAsc() {
        return employeeRepo.findAllSortedDTOByLoginAsc();
    }
}
