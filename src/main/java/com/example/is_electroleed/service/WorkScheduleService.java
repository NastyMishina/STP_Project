package com.example.is_electroleed.service;
import com.example.is_electroleed.entities.Employee;
import com.example.is_electroleed.entities.Project;
import com.example.is_electroleed.entities.WorkSchedule;
import com.example.is_electroleed.entities.DTO.WorkScheduleResponsibleDTO;
import com.example.is_electroleed.repositories.EmployeeRepo;
import com.example.is_electroleed.repositories.ProjectRepo;
import com.example.is_electroleed.repositories.WorkScheduleRepo;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для работы с расписаниями работы сотрудников.
 */
@Service
public class WorkScheduleService {

    @Autowired
    private WorkScheduleRepo workScheduleRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    /**
     * Получает список всех расписаний работы с возможностью поиска по ключевому слову.
     *
     * @param keyword ключевое слово для поиска (может быть null, если поиск не требуется)
     * @return список всех расписаний работы
     */
    public List<WorkSchedule> getAllWorkSchedules(String keyword) {
        if (keyword != null) {
            return workScheduleRepo.search(keyword);
        }
        return workScheduleRepo.findAll();
    }

    /**
     * Получает список всех расписаний работы в виде DTO с возможностью поиска по ключевому слову.
     *
     * @param keyword ключевое слово для поиска (может быть null, если поиск не требуется)
     * @return список всех расписаний работы в виде DTO
     */
    public List<WorkScheduleResponsibleDTO> getAllWorkSchedulesDTO(String keyword) {
        if (keyword != null) {
            return workScheduleRepo.searchResp(keyword);
        }
        return workScheduleRepo.findAllAsDTO();
    }

    /**
     * Сохраняет новое расписание работы.
     *
     * @param workSchedule объект расписания работы для сохранения
     * @throws IllegalArgumentException если передан null объект
     */
    public void save(WorkSchedule workSchedule) {
        if (workSchedule == null) {
            throw new IllegalArgumentException("Запись расписания работ не может быть null");
        }
        try {
            Optional<Project> project = projectRepo.findById(workSchedule.getIDProject().getID());
            Optional<Employee> employee = employeeRepo.findById(workSchedule.getIDProject().getID());
            if (project.isPresent() || employee.isPresent()) {
                throw new EntityNotFoundException("Проект с таким кодом не существует");
            }
            workScheduleRepo.save(workSchedule);
        } catch (Exception e) {
            throw new ServiceException("Ошибка при сохранении записи расписания работ. Введите данные существующего проекта или сотрудника", e);
        }
    }

    /**
     * Обновляет существующее расписание работы.
     *
     * @param workSchedule объект расписания работы с обновленными данными
     * @throws IllegalArgumentException если запись расписания не найдена
     */
    public void edit(WorkSchedule workSchedule) {
        if (workSchedule != null) {
            WorkSchedule existingSchedule = workScheduleRepo.findById(workSchedule.getID())
                    .orElseThrow(() -> new IllegalArgumentException("Запись расписания не найдена"));

            if (workSchedule.getIDProject() != null) {
                Project project = projectRepo.findById(workSchedule.getIDProject().getID())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Проект с ID " + workSchedule.getIDProject().getID() + " не найден"));
                existingSchedule.setIDProject(project);
            }

            if (workSchedule.getEmployee() != null) {
                Employee employee = employeeRepo.findById(workSchedule.getEmployee().getID())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Сотрудник с ID " + workSchedule.getEmployee().getID() + " не найден"));
                existingSchedule.setEmployee(employee);
            }

            existingSchedule.setName(workSchedule.getName());
            existingSchedule.setStartDate(workSchedule.getStartDate());
            existingSchedule.setEndDate(workSchedule.getEndDate());
            existingSchedule.setLaborIntensity(workSchedule.getLaborIntensity());
            existingSchedule.setStatus(workSchedule.getStatus());

            workScheduleRepo.save(existingSchedule);
        }
    }

    /**
     * Удаляет расписание работы по ID.
     *
     * @param id идентификатор расписания для удаления
     * @throws RuntimeException если возникла ошибка при удалении
     */
    public void deleteById(int id) {
        try {
            workScheduleRepo.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении расписания с ID " + id + ": " + e.getMessage());
        }
    }

    /**
     * Получает список всех расписаний работы, отсортированных по дате начала в порядке убывания.
     *
     * @return список расписаний работы, отсортированных по дате начала в порядке убывания
     */
    public List<WorkSchedule> findAllSortedByStartDateDesc() {
        return workScheduleRepo.findAll(Sort.by(Sort.Order.desc("startDate")));
    }

    /**
     * Получает список всех расписаний работы, отсортированных по дате начала в порядке возрастания.
     *
     * @return список расписаний работы, отсортированных по дате начала в порядке возрастания
     */
    public List<WorkSchedule> findAllSortedByStartDateAsc() {
        return workScheduleRepo.findAll(Sort.by(Sort.Order.asc("startDate")));
    }

    /**
     * Получает список всех расписаний работы, отсортированных по дате окончания в порядке убывания.
     *
     * @return список расписаний работы, отсортированных по дате окончания в порядке убывания
     */
    public List<WorkSchedule> findAllSortedByEndDateDesc() {
        return workScheduleRepo.findAll(Sort.by(Sort.Order.desc("endDate")));
    }

    /**
     * Получает список всех расписаний работы, отсортированных по дате окончания в порядке возрастания.
     *
     * @return список расписаний работы, отсортированных по дате окончания в порядке возрастания
     */
    public List<WorkSchedule> findAllSortedByEndDateAsc() {
        return workScheduleRepo.findAll(Sort.by(Sort.Order.asc("endDate")));
    }

    /**
     * Получает количество задач для каждого проекта по статусу.
     *
     * @return карта, где ключ - название проекта, а значение - карта статусов и количества задач по каждому статусу
     */
    public Map<String, Map<String, Long>> getTaskCountsByProjectAndStatus() {
        Map<String, Map<String, Long>> result = new HashMap<>();
        List<Object[]> data = workScheduleRepo.countTasksByProjectAndStatus();
        for (Object[] row : data) {
            String projectName = (String) row[0];
            String status = (String) row[1];
            Long count = (Long) row[2];

            result.computeIfAbsent(projectName, k -> new HashMap<>()).put(status, count);
        }
        return result;
    }
}