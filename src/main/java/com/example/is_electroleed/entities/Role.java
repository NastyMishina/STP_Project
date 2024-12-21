package com.example.is_electroleed.entities;

/**
 * Перечисление, представляющее различные роли пользователей в системе.
 * Каждая роль определяет права доступа к различным частям приложения.
 * - ADMIN: Администратор с полными правами доступа.
 * - ESTIMATOR: Ответственный за составление смет.
 * - SCHEDULER: Ответственный за планирование и управление графиками работ.
 * - PROJECT_MANAGER: Менеджер проекта, курирует выполнение проекта.
 * - PROJECT_MEMBER: Участник проекта, выполняет задачи в рамках проекта.
 */
public enum Role {
    ADMIN,
    ESTIMATOR,
    SCHEDULER,
    PROJECT_MANAGER,
    PROJECT_MEMBER
}
