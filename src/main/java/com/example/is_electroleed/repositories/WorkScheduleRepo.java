package com.example.is_electroleed.repositories;

import com.example.is_electroleed.entities.WorkSchedule;
import com.example.is_electroleed.entities.DTO.WorkScheduleResponsibleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью WorkSchedule (График работ). Этот интерфейс расширяет JpaRepository и
 * предоставляет методы для поиска, подсчета задач по проектам и статусам, а также получения графиков работ с дополнительной информацией.
 */
@Repository
public interface WorkScheduleRepo extends JpaRepository<WorkSchedule, Integer> {

    /**
     * Поиск графиков работ по ключевому слову. Метод ищет по всем полям сущности WorkSchedule, включая
     * ID записи, ID проекта, название работы, даты начала и окончания, интенсивность труда, ответственного сотрудника
     * и статус.
     *
     * @param keyword Ключевое слово для поиска в различных полях.
     * @return Список графиков работ, соответствующих запросу.
     */
    @Query("SELECT w FROM WorkSchedule w WHERE CONCAT(CAST(w.IDRecord AS string), CAST(w.IDProject AS string), w.name, " +
            "CAST(w.startDate AS string), CAST(w.endDate AS string), CAST(w.laborIntensity AS string), " +
            "CAST(w.employee AS string), w.status) LIKE %:keyword%")
    List<WorkSchedule> search(String keyword);

    /**
     * Поиск графиков работ с дополнительной информацией о проекте и ответственном сотруднике.
     * Метод возвращает информацию о проекте, на котором выполняется работа, включая название проекта,
     * даты начала и окончания, интенсивность труда, полное имя ответственного сотрудника и статус работы.
     *
     * @param keyword Ключевое слово для поиска по имени работы.
     * @return Список DTO объектов с информацией о графиках работ и связанных проектах.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.WorkScheduleResponsibleDTO(" +
            "w.IDProject.IDProject, p.name, w.name, p.startDate, " +
            "p.endDate, w.laborIntensity, e.fullName, w.status) " +
            "FROM WorkSchedule w " +
            "JOIN w.IDProject p " +
            "JOIN w.employee e " +
            "WHERE w.name LIKE CONCAT('%', :keyword, '%')")
    List<WorkScheduleResponsibleDTO> searchResp(@Param("keyword") String keyword);

    /**
     * Получение всех графиков работ с дополнительной информацией о проекте и ответственном сотруднике.
     * Метод возвращает информацию о проекте, на котором выполняется работа, включая название проекта,
     * даты начала и окончания, интенсивность труда, полное имя ответственного сотрудника и статус работы.
     *
     * @return Список DTO объектов с информацией о всех графиках работ и связанных проектах.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.WorkScheduleResponsibleDTO(" +
            "w.IDProject.IDProject, p.name, w.name, p.startDate, " +
            "p.endDate, w.laborIntensity, e.fullName, w.status) " +
            "FROM WorkSchedule w " +
            "JOIN w.IDProject p " +
            "JOIN w.employee e ")
    List<WorkScheduleResponsibleDTO> findAllAsDTO();

    /**
     * Подсчет количества задач (графиков работ) по проектам и статусам.
     * Этот метод возвращает количество задач по каждому проекту и их статусу.
     *
     * @return Список объектов, содержащих имя проекта, статус работы и количество задач с этим статусом.
     */
    @Query("SELECT p.name, ws.status, COUNT(ws) FROM WorkSchedule ws " +
            "JOIN ws.IDProject p " +
            "GROUP BY p.name, ws.status " +
            "ORDER BY p.name")
    List<Object[]> countTasksByProjectAndStatus();
}
