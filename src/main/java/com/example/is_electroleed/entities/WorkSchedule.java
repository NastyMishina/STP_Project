package com.example.is_electroleed.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Класс, представляющий запись графика работ в проекте. Этот класс является сущностью, связанной с таблицей "work_schedule" в базе данных.
 * Содержит информацию о выполнении конкретных задач в проекте, включая время начала и окончания работы, трудозатраты, ответственного сотрудника и статус задачи.
 *
 * Атрибуты:
 * - IDRecord: Уникальный идентификатор записи в графике работ.
 * - IDProject: Связь с проектом (сущность `Project`), к которому относится данный график работ.
 * - name: Название работы или задачи.
 * - startDate: Дата начала работы.
 * - endDate: Дата окончания работы.
 * - laborIntensity: Трудозатраты (в человеко-часах).
 * - employee: Связь с сотрудником (сущность `Employee`), ответственным за выполнение работы.
 * - status: Статус выполнения работы (например, "запланировано", "в процессе", "завершено").
 */
@Setter
@Entity
@Table(name = "work_schedule")
public class WorkSchedule {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_record")
        private Integer IDRecord;

        @Getter
        @ManyToOne
        @JoinColumn(name = "id_project", referencedColumnName = "id_project")
        private Project IDProject;

        @Getter
        private String name;

        @Getter
        @Column(name = "start_date")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date startDate;

        @Getter
        @Column(name = "end_date")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date endDate;

        @Getter
        @Column(name = "labor_intensity")
        private int laborIntensity;

        @Getter
        @ManyToOne
        @JoinColumn(name = "work_responsible", referencedColumnName = "id_employee")
        private Employee employee;

        @Getter
        private String status;

        public Integer getID() {
                return IDRecord;
        }
}
