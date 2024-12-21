package com.example.is_electroleed.entities.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Data Transfer Object (DTO) для представления графика работ с ответственным.
 *
 * Этот класс используется для передачи информации о графике работ, включая проект, название работы, сроки выполнения,
 * трудозатраты, ответственного за выполнение работы сотрудника и статус работы.
 */
@Getter
@Setter
public class WorkScheduleResponsibleDTO {
    private Integer projectId;
    private String projectName;
    private String workName;
    private Date startDate;
    private Date endDate;
    private Integer laborIntensity;
    private String responsibleFullName;
    private String status;

    /**
     * Конструктор для создания объекта WorkScheduleResponsibleDTO.
     *
     * @param projectId Идентификатор проекта.
     * @param projectName Название проекта.
     * @param workName Название работы.
     * @param startDate Дата начала работы.
     * @param endDate Дата окончания работы.
     * @param laborIntensity Трудозатраты для работы.
     * @param responsibleFullName Полное имя ответственного сотрудника.
     * @param status Статус выполнения работы.
     */
    public WorkScheduleResponsibleDTO(Integer projectId, String projectName, String workName, Date startDate, Date endDate,
                           Integer laborIntensity, String responsibleFullName, String status) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.workName = workName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.laborIntensity = laborIntensity;
        this.responsibleFullName = responsibleFullName;
        this.status = status;
    }
}
