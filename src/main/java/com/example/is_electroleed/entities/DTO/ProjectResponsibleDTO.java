package com.example.is_electroleed.entities.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Data Transfer Object (DTO) для представления проекта с ответственным за проект и его бюджетом.
 * Этот класс используется для передачи информации о проекте, включая его идентификатор, название, клиента,
 * даты начала и окончания, ответственного за проект сотрудника и бюджет проекта.
 */
@Getter
@Setter
public class ProjectResponsibleDTO {
    private Integer IDProject;
    private String nameProject;
    private String client;
    private Date startDate;
    private Date endDate;
    private String fullName;
    private double budget;

    /**
     * Конструктор для создания объекта ProjectResponsibleDTO.
     *
     * @param IDProject Идентификатор проекта.
     * @param nameProject Название проекта.
     * @param client Клиент проекта.
     * @param startDate Дата начала проекта.
     * @param endDate Дата окончания проекта.
     * @param fullName Полное имя ответственного за проект сотрудника.
     * @param budget Бюджет проекта.
     */
    public ProjectResponsibleDTO(int IDProject, String nameProject, String client,
                                 Date startDate, Date endDate, String fullName, double budget) {
        this.IDProject = IDProject;
        this.nameProject = nameProject;
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fullName = fullName;
        this.budget = budget;
    }
}
