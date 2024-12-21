package com.example.is_electroleed.repositories;

import com.example.is_electroleed.entities.DTO.EmployeeDTO;
import com.example.is_electroleed.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью Employee. Этот интерфейс расширяет JpaRepository,
 * что позволяет использовать стандартные CRUD операции, а также добавляет кастомные запросы
 * для поиска и сортировки сотрудников.
 */
@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    /**
     * Поиск сотрудников по ключевому слову в полях ID, имени или должности.
     * Метод возвращает список сотрудников, чьи данные содержат это ключевое слово.
     *
     * @param keyword Ключевое слово для поиска.
     * @return Список сотрудников, соответствующих поисковому запросу.
     */
    @Query("SELECT e FROM Employee e WHERE CONCAT(e.IDEmployee, e.fullName, e.position) LIKE %?1%")
    List<Employee> searchAll(String keyword);

    /**
     * Поиск сотрудников с указанием их логинов, с фильтрацией по ключевому слову в полях
     * ID, имени, должности или логине.
     * Метод возвращает список DTO объектов, содержащих информацию о сотрудниках и их логинах.
     *
     * @param keyword Ключевое слово для поиска.
     * @return Список DTO сотрудников с логинами, соответствующих запросу.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.EmployeeDTO(" +
            "e.IDEmployee, e.fullName, e.position, u.login) " +
            "FROM Employee e " +
            "JOIN User u ON e.account.IDUser = u.IDUser " +
            "WHERE CONCAT(e.IDEmployee, e.fullName, e.position, u.login) LIKE %?1%")
    List<EmployeeDTO> searchAllWithUsernames(String keyword);

    /**
     * Получение всех сотрудников в виде DTO объектов, включая логины, без фильтрации.
     * Этот метод возвращает полный список сотрудников, включая их логины.
     *
     * @return Список DTO сотрудников с логинами.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.EmployeeDTO(" +
            "e.IDEmployee, e.fullName, e.position, u.login) " +
            "FROM Employee e " +
            "JOIN User u ON e.account.IDUser = u.IDUser ")
    List<EmployeeDTO> searchAllDTO();

    /**
     * Получение всех сотрудников в виде DTO объектов, отсортированных по логину в порядке возрастания.
     *
     * @return Список сотрудников, отсортированных по логину по возрастанию.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.EmployeeDTO(e.IDEmployee, e.fullName, e.position, a.login) " +
            "FROM Employee e JOIN e.account a ORDER BY a.login ASC")
    List<EmployeeDTO> findAllSortedDTOByLoginAsc();

    /**
     * Получение всех сотрудников в виде DTO объектов, отсортированных по логину в порядке убывания.
     *
     * @return Список сотрудников, отсортированных по логину по убыванию.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.EmployeeDTO(e.IDEmployee, e.fullName, e.position, a.login) " +
            "FROM Employee e JOIN e.account a ORDER BY a.login desc ")
    List<EmployeeDTO> findAllSortedDTOByLoginDesc();

    /**
     * Получение всех сотрудников в виде DTO объектов, отсортированных по должности в порядке убывания.
     *
     * @return Список сотрудников, отсортированных по должности по убыванию.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.EmployeeDTO(e.IDEmployee, e.fullName, e.position, a.login) " +
            "FROM Employee e JOIN e.account a ORDER BY e.position desc ")
    List<EmployeeDTO> findAllSortedDTOByPositionDesc();

    /**
     * Получение всех сотрудников в виде DTO объектов, отсортированных по должности в порядке возрастания.
     *
     * @return Список сотрудников, отсортированных по должности по возрастанию.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.EmployeeDTO(e.IDEmployee, e.fullName, e.position, a.login) " +
            "FROM Employee e JOIN e.account a ORDER BY e.position asc ")
    List<EmployeeDTO> findAllSortedDTOByPositionAsc();
}
