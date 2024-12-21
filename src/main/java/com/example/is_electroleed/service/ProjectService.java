package com.example.is_electroleed.service;
import com.example.is_electroleed.entities.Employee;
import com.example.is_electroleed.entities.Project;
import com.example.is_electroleed.entities.DTO.ProjectResponsibleDTO;
import com.example.is_electroleed.repositories.EmployeeRepo;
import com.example.is_electroleed.repositories.ProjectRepo;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с данными проектов.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private EmployeeRepo employeeRepo;

    /**
     * Проверяет наличие проекта по идентификатору.
     *
     * @param id идентификатор проекта.
     * @return объект Optional, содержащий проект, если найден.
     * @throws EntityNotFoundException если проект с указанным ID не найден.
     */
    public Optional<Project> checkPresence(int id) {
        Optional<Project> projectOptional = projectRepo.findById(id);
        if (projectOptional.isEmpty()) {
            throw new EntityNotFoundException("Проект с ID " + id + " не найден");
        }
        return projectOptional;
    }

    /**
     * Получает все проекты. Если передан поисковый ключ, возвращаются только те проекты, которые соответствуют ключу.
     *
     * @param keyword строка для поиска. Если null, возвращаются все проекты.
     * @return список проектов, удовлетворяющих критериям поиска.
     */
    public List<Project> getAllProjects(String keyword) {
        if (keyword != null){
            return projectRepo.search(keyword);
        }
        return projectRepo.findAll();
    }

    /**
     * Получает все проекты с данными о лице, ответственном за проект.
     * Если передан поисковый ключ, возвращаются только те проекты, которые соответствуют ключу.
     *
     * @param keyword строка для поиска. Если null, возвращаются все проекты с ответственными.
     * @return список объектов ProjectResponsibleDTO, содержащих информацию о проекте и ответственном.
     */
    public List<ProjectResponsibleDTO> getAllProjectsDTO(String keyword) {
        if (keyword != null){
            return projectRepo.searchWithResponsible(keyword);
        }
        return projectRepo.findAllAsDTO();
    }

    /**
     * Сохраняет новый проект в базе данных.
     *
     * @param project объект проекта для сохранения.
     * @throws IllegalArgumentException если объект проекта равен null.
     * @throws ServiceException если произошла ошибка при сохранении проекта.
     */
    public void save(Project project) {
        if (project == null){
            throw new IllegalArgumentException("Проект не может быть null");
        }
        try {
        projectRepo.save(project);
        } catch (Exception ex) {
            throw new ServiceException("Ошибка при сохранении проекта. Возможно, Вы указали не существующего сотрудника", ex);
        }

    }

    /**
     * Редактирует существующий проект.
     * Обновляет данные проекта, такие как название, клиент, дата начала и окончания, ответственный и бюджет.
     *
     * @param project объект проекта с новыми данными для обновления.
     * @throws IllegalArgumentException если проект не найден по указанному ID.
     * @throws ServiceException если произошла ошибка при редактировании проекта.
     */
    public void edit(Project project) {
        if (project.getID() != null) {
            Project existingProject = projectRepo.findById(project.getID())
                    .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));
            existingProject.setName(project.getName());
            existingProject.setClient(project.getClient());
            existingProject.setStartDate(project.getStartDate());
            existingProject.setEndDate(project.getEndDate());
            if (project.getResponsible() != null) {
                Employee responsible = employeeRepo.findById(project.getResponsible().getID())
                        .orElseThrow(() -> new IllegalArgumentException("Аккаунт не найден"));
                existingProject.setResponsible(responsible);
            }
            existingProject.setBudget(project.getBudget());
            projectRepo.save(existingProject);
        }
    }

    /**
     * Удаляет проект по идентификатору.
     *
     * @param id идентификатор проекта.
     * @throws ServiceException если произошла ошибка при удалении проекта.
     */
    public void deleteById(int id) {
        try {
            projectRepo.deleteById(id);
        } catch (Exception ex) {
            throw new ServiceException("Ошибка при удалении проекта с ID " + id, ex);
        }
    }

    /**
     * Возвращает список всех проектов, отсортированных по дате начала (startDate) в порядке убывания.
     *
     * @return список проектов, отсортированных по полю startDate в порядке убывания.
     */
    public List<Project> findAllSortedByStartDateDesc() {
        return projectRepo.findAll(Sort.by(Sort.Order.desc("startDate")));
    }

    /**
     * Возвращает список всех проектов, отсортированных по дате окончания (endDate) в порядке убывания.
     *
     * @return список проектов, отсортированных по полю endDate в порядке убывания.
     */
    public List<Project> findAllSortedByEndDateDesc() {
        return projectRepo.findAll(Sort.by(Sort.Order.desc("endDate")));
    }

    /**
     * Возвращает список всех проектов, отсортированных по дате начала (startDate) в порядке возрастания.
     *
     * @return список проектов, отсортированных по полю startDate в порядке возрастания.
     */
    public List<Project> findAllSortedByStartDateAsc() {
        return projectRepo.findAll(Sort.by(Sort.Order.asc("startDate")));
    }

    /**
     * Возвращает список всех проектов, отсортированных по дате окончания (endDate) в порядке возрастания.
     *
     * @return список проектов, отсортированных по полю endDate в порядке возрастания.
     */
    public List<Project> findAllSortedByEndDateAsc() {
        return projectRepo.findAll(Sort.by(Sort.Order.asc("endDate")));
    }

    /**
     * Возвращает список всех проектов.
     *
     * @return список всех проектов.
     */
    public List<Project> findAll() {
        return projectRepo.findAll();
    }

    /**
     * Получает проект по его идентификатору.
     *
     * @param id идентификатор проекта.
     * @return проект с указанным идентификатором.
     */
    public Project findById(Integer id) {
        try {
            Optional<Project> projectOptional = projectRepo.findById(id);
            if (projectOptional.isEmpty()) {
                throw new IllegalArgumentException("Проекта с ID " + id + " не существует");
            }
            return projectOptional.get();
        } catch (Exception e) {
            throw new ServiceException("Ошибка при сохранении записи сметы. Введите данные существующего проекта", e);
        }
    }

    /**
     * Получает количество активных проектов.
     *
     * @return количество активных проектов.
     */
    public Long getActiveProjectsCount() {
        return projectRepo.countActiveProjects();
    }

    /**
     * Получает количество завершенных проектов.
     *
     * @return количество завершенных проектов.
     */
    public Long getCompletedProjectsCount() {
        return projectRepo.countCompletedProjects();
    }

    /**
     * Получает общее количество проектов.
     *
     * @return общее количество проектов.
     */
    public Object getTotalProjects() {
        return projectRepo.countProjects();
    }
}