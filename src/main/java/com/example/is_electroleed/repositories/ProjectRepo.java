package com.example.is_electroleed.repositories;

import com.example.is_electroleed.entities.Project;
import com.example.is_electroleed.entities.DTO.ProjectResponsibleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью Project (Проект). Этот интерфейс расширяет JpaRepository и
 * предоставляет методы для поиска, фильтрации, получения информации о проектах и подсчета статистики.
 */
@Repository
public interface ProjectRepo extends JpaRepository<Project, Integer> {

    /**
     * Поиск проектов по ключевому слову. Метод ищет по всем полям сущности Project, включая
     * ID проекта, название, клиента, даты начала и окончания, ответственного сотрудника и бюджет.
     *
     * @param keyword Ключевое слово для поиска в различных полях.
     * @return Список проектов, соответствующих запросу.
     */
    @Query("SELECT p FROM Project p WHERE CONCAT(p.IDProject, p.name, p.client, " +
            "p.startDate, p.endDate, p.responsible, p.budget) LIKE %?1% ")
    List<Project> search(String keyword);

    /**
     * Поиск проектов с данными об ответственном сотруднике по ключевому слову в названии проекта.
     * Метод возвращает DTO объекты с данными о проекте, ответственном сотруднике, клиенте,
     * датах начала и окончания и бюджете.
     *
     * @param keyword Ключевое слово для поиска в названиях проектов.
     * @return Список DTO объектов с проектами и ответственными сотрудниками.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.ProjectResponsibleDTO(" +
            "p.IDProject, p.name, p.client, p.startDate, p.endDate, e.fullName, p.budget) " +
            "FROM Project p " +
            "JOIN p.responsible e WHERE p.name LIKE CONCAT('%', :keyword, '%')")
    List<ProjectResponsibleDTO> searchWithResponsible(String keyword);

    /**
     * Получение всех проектов в виде DTO объектов, включая информацию о проекте, клиенте, датах,
     * ответственном сотруднике и бюджете.
     *
     * @return Список всех проектов в виде DTO объектов.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.ProjectResponsibleDTO(" +
            "p.IDProject, p.name, p.client, p.startDate, p.endDate, e.fullName, p.budget)" +
            "FROM Project p JOIN p.responsible e")
    List<ProjectResponsibleDTO> findAllAsDTO();

    /**
     * Подсчет количества активных проектов, у которых дата окончания либо не указана, либо больше текущей даты.
     *
     * @return Количество активных проектов.
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.endDate IS NULL OR p.endDate > CURRENT_DATE")
    Long countActiveProjects();

    /**
     * Подсчет количества завершенных проектов, у которых дата окончания меньше или равна текущей дате.
     *
     * @return Количество завершенных проектов.
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE p.endDate <= CURRENT_DATE")
    Long countCompletedProjects();

    /**
     * Подсчет общего количества проектов в базе данных.
     *
     * @return Общее количество проектов.
     */
    @Query("SELECT COUNT(p) FROM Project p")
    Long countProjects();
}
