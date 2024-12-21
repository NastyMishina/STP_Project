package com.example.is_electroleed.entities.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) для представления сотрудника.
 *
 * Этот класс используется для передачи информации о сотруднике между слоями приложения.
 * Он содержит данные, такие как идентификатор, полное имя, должность и логин сотрудника.
 */
@Getter
@Setter
public class EmployeeDTO {

    private Integer id;
    private String fullName;
    private String position;
    private String login;

    /**
     * Конструктор для создания объекта EmployeeDTO.
     *
     * @param id Идентификатор сотрудника.
     * @param fullName Полное имя сотрудника.
     * @param position Должность сотрудника.
     * @param username Логин сотрудника.
     */
    public EmployeeDTO(Integer id, String fullName, String position, String username) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
        this.login = username;
    }
}
