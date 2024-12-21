package com.example.is_electroleed.entities;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Класс представляет проект, который является сущностью, соответствующей таблице "projects" в базе данных.
 * Включает информацию о проекте, его ответственном сотруднике, бюджете, а также список смет и графиков работ, связанных с этим проектом.
 */
@Setter
@Entity
@Table(name="projects")
public class Project {

    /**
     * Уникальный идентификатор проекта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_project")
    private Integer IDProject;

    /**
     * Название проекта.
     */
    @Getter
    private String name;

    /**
     * Заказчик проекта.
     */
    @Getter
    private String client;

    /**
     * Дата начала проекта.
     */
    @Getter
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * Дата завершения проекта.
     */
    @Getter
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * Ответственный сотрудник за проект.
     */
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="responsible", referencedColumnName="id_employee")
    private Employee responsible;

    /**
     * Бюджет проекта.
     */
    @Getter
    private Double budget;

    /**
     * Список графиков работ для этого проекта.
     */
    @Getter
    @OneToMany(mappedBy = "IDProject", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WorkSchedule> workSchedule;

    /**
     * Список смет для этого проекта.
     */
    @Getter
    @OneToMany(mappedBy = "IDProject", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Estimate> estimate;

    /**
     * Получает уникальный идентификатор проекта.
     *
     * @return идентификатор проекта.
     */
    public Integer getID() { return IDProject; }

}
