package com.example.is_electroleed.entities;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Класс, представляющий учетную запись пользователя. Этот класс является сущностью, связанной с таблицей "user" в базе данных.
 * Каждый пользователь имеет уникальный идентификатор, логин, пароль и роль.
 * Также у пользователя может быть привязанный сотрудник (сущность `Employee`), который содержит дополнительную информацию о пользователе.
 * Атрибуты:
 * - IDUser: Уникальный идентификатор пользователя.
 * - role: Роль пользователя в системе, представлена перечислением `Role`.
 * - login: Логин пользователя.
 * - password: Пароль пользователя, который хранится в зашифрованном виде.
 * - employee: Связь с сущностью `Employee`, предоставляющей дополнительные данные о пользователе.
 */
 @Setter
@Entity
@Table(name = "user")
@ToString
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer IDUser;

    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Column(name = "login")
    private String login;

    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Getter
    @OneToOne(mappedBy = "account", cascade = CascadeType.REMOVE)
    private Employee employee;


    public Integer getID() {
        return IDUser;
    }

}
