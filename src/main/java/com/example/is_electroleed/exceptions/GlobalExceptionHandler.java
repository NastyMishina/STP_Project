package com.example.is_electroleed.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.persistence.EntityNotFoundException;

/**
 * Класс обработки глобальных исключений. Все исключения, возникающие в приложении,
 * будут перехвачены и обработаны этим классом. В зависимости от типа исключения,
 * возвращается представление с соответствующим сообщением об ошибке.
 * - EntityNotFoundException: Обрабатывает ошибку, когда запрашиваемая сущность не найдена.
 * - IllegalStateException: Обрабатывает ошибку, возникающую из-за некорректного состояния системы.
 * - DatabaseException: Обрабатывает ошибки базы данных.
 * - Exception: Обрабатывает непредвиденные ошибки.
 * - DataIntegrityViolationException: Обрабатывает ошибки целостности данных (например, нарушение ограничений базы данных).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка исключения EntityNotFoundException. Вызывается, когда сущность не найдена.
     *
     * @param ex Исключение, которое произошло.
     * @param model Модель, в которую добавляется сообщение об ошибке.
     * @return Имя представления для отображения ошибки.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    /**
            * Обработка исключения IllegalStateException. Вызывается, когда система находится в некорректном состоянии.
            *
            * @param ex Исключение, которое произошло.
     * @param model Модель, в которую добавляется сообщение об ошибке.
     * @return Имя представления для отображения ошибки.
     */
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    /**
     * Обработка исключения DatabaseException. Вызывается, когда возникает ошибка базы данных.
     *
     * @param ex Исключение, которое произошло.
     * @param model Модель, в которую добавляется сообщение об ошибке.
     * @return Имя представления для отображения ошибки.
     */
    @ExceptionHandler(DatabaseException.class)
    public String handleDatabaseException(DatabaseException ex, Model model) {
        model.addAttribute("error", "Произошла ошибка базы данных: " + ex.getMessage());
        return "error";
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(Model model) {
        model.addAttribute("error", "Произошла ошибка добавления: указанного Вами объекта не существует!" );
        return "error";
    }

    @ExceptionHandler(SpelEvaluationException.class)
    public String handleSpelEvaluationException(SpelEvaluationException ex, Model model) {
        model.addAttribute("error", "Ошибка: Укажите существующий код проекта" + ex.getMessage());
        return "error";
    }
    /**
     * Обработка общих исключений. Вызывается для всех непойманных исключений.
     *
     * @param ex Исключение, которое произошло.
     * @param model Модель, в которую добавляется сообщение об ошибке.
     * @return Имя представления для отображения ошибки.
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("error", "Непредвиденная ошибка: " + ex.getMessage());
        return "error";
    }

    /**
     * Обработка исключения DataIntegrityViolationException. Вызывается, когда происходит нарушение целостности данных.
     *
     * @param ex Исключение, которое произошло.
     * @param model Модель, в которую добавляется сообщение об ошибке.
     * @return Имя представления для отображения ошибки.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("error", "Ошибка целостности данных: " + ex.getMostSpecificCause().getMessage());
        return "error";
    }
}
