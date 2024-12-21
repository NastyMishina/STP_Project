package com.example.is_electroleed.entities.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Data Transfer Object (DTO) для представления сметы и ответственного сотрудника.
 *
 * Этот класс используется для передачи информации о смете, проекте, сотруднике и связанных с ними данных между слоями приложения.
 * Он содержит данные, такие как идентификатор сметы, название проекта, имя сотрудника, статью расходов, единицу измерения,
 * количество, цену и дату записи.
 */
@Getter
@Setter
public class EstimateResponsibleDTO {
    private Integer estimateId;
    private String projectName;
    private String employeeName;
    private String expenseItem;
    private String unitsMeasurement;
    private double amount;
    private double price;
    private Date recordDate;

    /**
     * Конструктор для создания объекта EstimateResponsibleDTO.
     *
     * @param estimateId Идентификатор сметы.
     * @param projectName Название проекта.
     * @param employeeName Имя сотрудника.
     * @param expenseItem Статья расходов.
     * @param unitsMeasurement Единица измерения.
     * @param amount Количество.
     * @param price Цена за единицу.
     * @param recordDate Дата записи.
     */
    public EstimateResponsibleDTO(Integer estimateId, String projectName, String employeeName, String expenseItem,
                                  String unitsMeasurement, double amount, double price, Date recordDate) {
        this.estimateId = estimateId;
        this.projectName = projectName;
        this.employeeName = employeeName;
        this.expenseItem = expenseItem;
        this.unitsMeasurement = unitsMeasurement;
        this.amount = amount;
        this.price = price;
        this.recordDate = recordDate;

    }
}
