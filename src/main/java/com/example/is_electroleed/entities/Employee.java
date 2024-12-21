package com.example.is_electroleed.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс представляет сотрудника компании, являясь сущностью, соответствующей таблице "employees" в базе данных.
 * Он содержит информацию о сотруднике, его учетной записи, должности, а также список проектов и графиков работ, связанных с этим сотрудником.
 */
@Setter
@Entity
@Table(name="employees")
public class Employee {

    /**
     * Уникальный идентификатор сотрудника.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_employee")
    private Integer IDEmployee;

    /**
     * Полное имя сотрудника.
     */
    @Getter
    private String fullName;

    /**
     * Должность сотрудника.
     */
    @Getter
    private String position;

    /**
     * Учетная запись пользователя, связанная с этим сотрудником.
     */
    @Getter
    @OneToOne
    @JoinColumn(name = "account", referencedColumnName="iduser")
    private User account;

    /**
     * Список проектов, в которых сотрудник является ответственным.
     */
    @Getter
    @OneToMany(mappedBy = "responsible", cascade = CascadeType.REMOVE)
    private List<Project> projects;

    /**
     * Список графиков работ, назначенных этому сотруднику.
     */
    @Getter
    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    private List<WorkSchedule> workSchedules;

    /**
     * Получает уникальный идентификатор сотрудника.
     *
     * @return идентификатор сотрудника.
     */
    public Integer getID() {return IDEmployee;}

}
