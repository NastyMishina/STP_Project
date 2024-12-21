package com.example.is_electroleed.entities.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) для представления бюджета проекта.
 *
 * Этот класс используется для передачи информации о названии проекта и его общем бюджете между слоями приложения.
 * Он содержит название проекта и общую сумму бюджета для проекта.
 */
@Setter
@Getter
public class ProjectBudgetDTO {

    private String projectName;

    private Double totalBudget;

    /**
     * Конструктор для создания объекта ProjectBudgetDTO.
     *
     * @param projectName Название проекта.
     * @param totalBudget Общий бюджет проекта.
     */
    public ProjectBudgetDTO(String projectName, Double totalBudget) {
        this.projectName = projectName;
        this.totalBudget = totalBudget;
    }
}