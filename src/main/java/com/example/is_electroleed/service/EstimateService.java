package com.example.is_electroleed.service;

import com.example.is_electroleed.entities.Employee;
import com.example.is_electroleed.entities.Estimate;
import com.example.is_electroleed.entities.DTO.EstimateResponsibleDTO;
import com.example.is_electroleed.entities.DTO.ProjectBudgetDTO;
import com.example.is_electroleed.entities.Project;
import com.example.is_electroleed.repositories.EmployeeRepo;
import com.example.is_electroleed.repositories.EstimateRepo;
import com.example.is_electroleed.repositories.ProjectRepo;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с данными смет.
 */
@Service
public class EstimateService {

    @Autowired
    private EstimateRepo estimateRepo;

    @Autowired
    private ProjectRepo projectRepo;

    /**
     * Получает все записи смет. Если передан поисковый ключ, возвращает только те записи, которые соответствуют ключу.
     *
     * @param keyword строка для поиска. Если null, возвращаются все сметы.
     * @return список смет, удовлетворяющих критериям поиска.
     */
    public List<Estimate> getAllEstimates(String keyword) {
        if (keyword != null) {
            return estimateRepo.search(keyword);
        }
        return estimateRepo.findAll();
    }

    /**
     * Получает все записи смет с данными о лице, ответственном за смету.
     * Если передан поисковый ключ, возвращаются только те записи, которые соответствуют ключу.
     *
     * @param keyword строка для поиска. Если null, возвращаются все сметы с ответственными.
     * @return список объектов EstimateResponsibleDTO, которые содержат информацию о смете и ответственном.
     */
    public List<EstimateResponsibleDTO> getAllEstimatesResp(String keyword) {
        if (keyword != null) {
            return estimateRepo.searchEstimateResp(keyword);
        }
        return estimateRepo.findAllAsDTO();
    }

    /**
     * Сохраняет новую запись сметы в базе данных.
     *
     * @param estimate объект сметы для сохранения.
     * @throws IllegalArgumentException если объект сметы равен null.
     * @throws ServiceException если произошла ошибка при сохранении сметы.
     */
    public void save(Estimate estimate) {
        if (estimate == null) {
            throw new IllegalArgumentException("Запись сметы не может быть null");
        }
        try {
            Optional<Project> project = projectRepo.findById(estimate.getIDProject().getID());
            if (project.isPresent()) {
                throw new EntityNotFoundException("Проект с таким кодом не существует");
            }
            estimateRepo.save(estimate);
        } catch (Exception e) {
            throw new ServiceException("Ошибка при сохранении записи сметы. Введите данные существующего проекта", e);
        }
    }

    /**
     * Редактирует существующую запись сметы.
     * Обновляет данные сметы, такие как проект, расходная статья, единицы измерения, количество, цена и дата записи.
     *
     * @param estimate объект сметы с новыми данными для обновления.
     * @throws IllegalArgumentException если смета равна null или не найдена.
     * @throws ServiceException если произошла ошибка при редактировании сметы.
     */
    public void edit(Estimate estimate) {
        if (estimate != null) {
            Estimate existingEstimate = estimateRepo.findById(estimate.getID())
                    .orElseThrow(() -> new IllegalArgumentException("Запись сметы не найдена"));

            if (estimate.getIDProject() != null) {
                Project project = projectRepo.findById(estimate.getIDProject().getID())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Проект с ID " + estimate.getIDProject().getID() + " не найден"));
                existingEstimate.setIDProject(project);
            }
            existingEstimate.setExpenseItem(estimate.getExpenseItem());
            existingEstimate.setUnitsMeasurement(estimate.getUnitsMeasurement());
            existingEstimate.setAmount(estimate.getAmount());
            existingEstimate.setPrice(estimate.getPrice());
            existingEstimate.setRecordDate(estimate.getRecordDate());

            estimateRepo.save(existingEstimate);
        }
    }

    /**
     * Удаляет запись сметы по идентификатору.
     *
     * @param id идентификатор записи сметы.
     * @throws RuntimeException если произошла ошибка при удалении сметы.
     */
    public void deleteById(int id) {
        try {
            estimateRepo.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении сметы с ID " + id + ": " + e.getMessage());
        }
    }

    /**
     * Возвращает список всех смет, отсортированных по ID проекта в порядке убывания.
     *
     * @return список смет, отсортированных по полю IDProject в порядке убывания.
     */
    public List<Estimate> findAllSortedByIDProjectDesc() {
        return estimateRepo.findAll(Sort.by(Sort.Order.desc("IDProject")));
    }

    /**
     * Возвращает список всех смет, отсортированных по ID проекта в порядке возрастания.
     *
     * @return список смет, отсортированных по полю IDProject в порядке возрастания.
     */
    public List<Estimate> findAllSortedByIDProjectAsc() {
        return estimateRepo.findAll(Sort.by(Sort.Order.asc("IDProject")));
    }

    /**
     * Возвращает список всех смет, отсортированных по дате записи (RecordDate) в порядке убывания.
     *
     * @return список смет, отсортированных по полю RecordDate в порядке убывания.
     */
    public List<Estimate> findAllSortedByRecordDateDesc() {
        return estimateRepo.findAll(Sort.by(Sort.Order.desc("RecordDate")));
    }

    /**
     * Возвращает список всех смет, отсортированных по дате записи (RecordDate) в порядке возрастания.
     *
     * @return список смет, отсортированных по полю RecordDate в порядке возрастания.
     */
    public List<Estimate> findAllSortedByRecordDateAsc() {
        return estimateRepo.findAll(Sort.by(Sort.Order.asc("RecordDate")));
    }

    /**
     * Получает данные о потраченном бюджете по каждому проекту.
     *
     * @return список объектов ProjectBudgetDTO, содержащих имя проекта и потраченный бюджет.
     */
    public List<ProjectBudgetDTO> getSpentBudgetByProjects() {
        return estimateRepo.countSpentBudget();
    }
}