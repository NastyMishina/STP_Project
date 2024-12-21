package com.example.is_electroleed.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Класс представляет смету для проекта, содержащую информацию о расходах, единицах измерения,
 * количестве, цене и дате записи.
 */
@Setter
@Entity
public class Estimate {

    /**
     * Уникальный идентификатор сметы.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDRecord;

    /**
     * Проект, к которому относится смета.
     */
    @Getter
    @ManyToOne
    @JoinColumn(name = "idproject", referencedColumnName="id_project", nullable = false)
    private Project IDProject;

    /**
     * Статья расходов в смете.
     */
    @Getter
    private String expenseItem;

    /**
     * Единицы измерения расхода.
     */
    @Getter
    private String unitsMeasurement;

    /**
     * Количество расхода.
     */
    @Getter
    private double amount;

    /**
     * Цена за единицу расхода.
     */
    @Getter
    private double price;

    /**
     * Дата записи сметы.
     */
    @Getter
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date recordDate;

    /**
     * Получает уникальный идентификатор сметы.
     *
     * @return идентификатор сметы.
     */
    public Integer getID() { return IDRecord; }
}
