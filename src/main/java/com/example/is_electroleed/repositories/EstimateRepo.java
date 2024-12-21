package com.example.is_electroleed.repositories;

import com.example.is_electroleed.entities.Estimate;
import com.example.is_electroleed.entities.DTO.EstimateResponsibleDTO;
import com.example.is_electroleed.entities.DTO.ProjectBudgetDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностью Estimate. Этот интерфейс расширяет JpaRepository и
 * предоставляет методы для поиска, фильтрации и получения данных о расходах по проектам.
 */
@Repository
public interface EstimateRepo extends JpaRepository<Estimate, Integer> {

    /**
     * Поиск оценок по ключевому слову. Метод ищет по всем полям сущности Estimate,
     * включая ID записи, ID проекта, наименования расходов, количество, цену, дату записи и единицу измерения.
     *
     * @param keyword Ключевое слово для поиска в различных полях.
     * @return Список оценок, соответствующих запросу.
     */
    @Query("SELECT e FROM Estimate e WHERE CONCAT(CAST(e.IDRecord AS string), CAST(e.IDProject AS string), e.expenseItem, " +
            "CAST(e.amount AS string), CAST(e.price AS string), " +
            "CAST(e.recordDate AS string), e.unitsMeasurement) LIKE %:keyword%")
    List<Estimate> search(String keyword);

    /**
     * Поиск записи сметы по ключевому слову, с получением информации об ответственном за проект сотруднике.
     * Метод возвращает DTO объекты с данными о проекте, ответственном сотруднике, расходных статьях, количестве,
     * цене, единице измерения и дате записи.
     *
     * @param keyword Ключевое слово для поиска в названиях проектов.
     * @return Список DTO оценок с данными о проекте и ответственном сотруднике.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.EstimateResponsibleDTO(" +
            "es.IDRecord, p.name, e.fullName, es.expenseItem, es.unitsMeasurement, es.amount, es.price, es.recordDate) " +
            "FROM Estimate es " +
            "JOIN es.IDProject p " +
            "JOIN p.responsible e " +
            "WHERE p.name LIKE CONCAT('%', :keyword, '%')")
    List<EstimateResponsibleDTO> searchEstimateResp(@Param("keyword") String keyword);

    /**
     * Получение всех смет в виде DTO объектов, включая информацию о проекте, ответственном сотруднике,
     * расходных статьях, количестве, цене, единице измерения и дате записи.
     *
     * @return Список всех оценок в виде DTO объектов.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.EstimateResponsibleDTO(" +
            "es.IDRecord, p.name, e.fullName, es.expenseItem, es.unitsMeasurement, es.amount, es.price, es.recordDate) " +
            "FROM Estimate es " +
            "JOIN es.IDProject p " +
            "JOIN p.responsible e")
    List<EstimateResponsibleDTO> findAllAsDTO();

    /**
     * Подсчет потраченного бюджета для каждого проекта. Метод вычисляет общую сумму расходов по проектам
     * на основе умножения цены на количество для каждой записи Estimate.
     *
     * @return Список DTO с данными о проекте и потраченном бюджете.
     */
    @Query("SELECT new com.example.is_electroleed.entities.DTO.ProjectBudgetDTO(" +
            "p.name, CAST(SUM(es.price * es.amount) AS DOUBLE) AS spentBudget) " +
            "FROM Estimate es " +
            "JOIN es.IDProject p " +
            "GROUP BY p.name")
    List<ProjectBudgetDTO> countSpentBudget();
}
