package com.example.is_electroleed.Controllers.WebControllers;

import com.example.is_electroleed.entities.DTO.EmployeeDTO;
import com.example.is_electroleed.entities.Project;
import com.example.is_electroleed.entities.WorkSchedule;
import com.example.is_electroleed.service.EmployeeService;
import com.example.is_electroleed.service.ProjectService;
import com.example.is_electroleed.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления веб-интерфейсом для планировщика (с ролью `SCHEDULER`).
 * Контроллер обрабатывает маршруты для управления графиками работ, проектами и сотрудниками.
 * Все методы в данном классе защищены аннотацией `@PreAuthorize`,
 * которая проверяет роль пользователя перед доступом к ресурсам.
 * Маршруты:
 * - `/home` — домашняя страница для планировщика.
 * - `/employee` — список сотрудников с возможностью фильтрации и сортировки.
 * - `/project` — список проектов с возможностью фильтрации и сортировки.
 * - `/work_schedule` — список графиков работ с возможностью фильтрации и сортировки.
 * Каждое действие сначала выполняет проверку доступа с помощью метода `checkAccess`,
 * чтобы убедиться, что пользователь авторизован и имеет нужные права.
 * Используемые сервисы:
 * - `EmployeeService` — сервис для работы с сотрудниками.
 * - `ProjectService` — сервис для работы с проектами.
 * - `WorkScheduleService` — сервис для работы с графиками работ.
 */
@Controller
@RequestMapping("/scheduler")
@PreAuthorize("hasRole('SCHEDULER')")
public class SchedulerWebController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private WorkScheduleService workScheduleService;

    /**
     * Отображает главную страницу для роли "Участник проектов".
     * Передает в модель:
     * Общее количество проектов.
     * Количество активных и завершенных проектов.
     * Статистику задач по проектам и статусам.
     *
     * @param model Модель для передачи данных в представление.
     * @return Имя представления: scheduler/home.
     */
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("totalProjects", projectService.getTotalProjects());
        model.addAttribute("activeProjectsCount", projectService.getActiveProjectsCount());
        model.addAttribute("completedProjectsCount", projectService.getCompletedProjectsCount());

        Map<String, Map<String, Long>> taskCounts = workScheduleService.getTaskCountsByProjectAndStatus();
        model.addAttribute("taskCounts", taskCounts);
        return "scheduler/home";
    }

    /**
     * Отображает список сотрудников с возможностью фильтрации и сортировки по логину или должности.
     *
     * @param model модель для передачи данных на представление
     * @param keyword ключевое слово для поиска сотрудников
     * @param sort поле для сортировки (например, "position" или "login")
     * @param order порядок сортировки (например, "asc" или "desc")
     * @return имя представления для отображения списка сотрудников
     */
    @GetMapping("/employee")
    public String viewEmpl(Model model, @Param("keyword") String keyword,
                           @RequestParam(required = false) String sort,
                           @RequestParam(required = false) String order) {
        List<EmployeeDTO> listEmployees;
        if ("position".equals(sort)) {
            listEmployees = "desc".equals(order) ? employeeService.findAllSortedDTOByPositionDesc() :
                    employeeService.findAllSortedDTOByPositionAsc();
        } else if ("login".equals(sort)) {
            listEmployees = "desc".equals(order) ? employeeService.findAllSortedDTOByLoginDesc() :
                    employeeService.findAllSortedDTOByLoginAsc();
        } else {
            listEmployees = employeeService.getAllEmployeesDTO(keyword);
        }
        model.addAttribute("listEmployees", listEmployees);
        model.addAttribute("keyword", keyword);
        return "scheduler/employee";
    }

    /**
     * Отображает список проектов с возможностью фильтрации и сортировки по дате начала и окончания.
     *
     * @param model модель для передачи данных на представление
     * @param keyword ключевое слово для поиска проектов
     * @param sort поле для сортировки (например, "startDate" или "endDate")
     * @param order порядок сортировки (например, "asc" или "desc")
     * @return имя представления для отображения списка проектов
     */
    @GetMapping("/project")
    public String viewProject(Model model, @Param("keyword") String keyword,
                              @RequestParam(required = false) String sort,
                              @RequestParam(required = false) String order) {
        List<Project> listProjects;
        if ("startDate".equals(sort)) {
            listProjects = "desc".equals(order) ? projectService.findAllSortedByStartDateDesc() :
                    projectService.findAllSortedByStartDateAsc();
        } else if ("endDate".equals(sort)) {
            listProjects = "desc".equals(order) ? projectService.findAllSortedByEndDateDesc() :
                    projectService.findAllSortedByEndDateAsc();
        } else {
            listProjects = projectService.getAllProjects(keyword);
        }
        model.addAttribute("listProjects", listProjects);
        model.addAttribute("keyword", keyword);
        return "scheduler/project";
    }

    /**
     * Отображает список графиков работ с возможностью фильтрации и сортировки по дате начала и окончания.
     *
     * @param model модель для передачи данных на представление
     * @param keyword ключевое слово для поиска графиков работ
     * @param sort поле для сортировки (например, "startDate" или "endDate")
     * @param order порядок сортировки (например, "asc" или "desc")
     * @return имя представления для отображения списка графиков работ
     */
    @GetMapping("/work_schedule")
    public String viewWorkSchedule(Model model, @Param("keyword") String keyword,
                                   @RequestParam(required = false) String sort,
                                   @RequestParam(required = false) String order) {
        List<WorkSchedule> listWorkSchedules;
        if ("startDate".equals(sort)) {
            listWorkSchedules = "desc".equals(order) ?
                    workScheduleService.findAllSortedByStartDateDesc() :
                    workScheduleService.findAllSortedByStartDateAsc();
        } else if ("endDate".equals(sort)) {
            listWorkSchedules = "desc".equals(order) ?
                    workScheduleService.findAllSortedByEndDateDesc() :
                    workScheduleService.findAllSortedByEndDateAsc();
        } else {
            listWorkSchedules = workScheduleService.getAllWorkSchedules(keyword);
        }

        model.addAttribute("listWorkSchedules", listWorkSchedules);
        model.addAttribute("keyword", keyword);
        return "scheduler/work_schedule";
    }

    /**
     * Создает новый график работ.
     *
     * @param workSchedule объект для сохранения нового графика работ
     * @return редирект на страницу с графиками работ
     */
    @PostMapping("/new_work_schedule")
    public String newWorkSchedule(@ModelAttribute("workSchedule") WorkSchedule workSchedule) {
        workScheduleService.save(workSchedule);
        return "redirect:/scheduler/work_schedule";
    }

    /**
     * Сохраняет изменения в графике работ.
     *
     * @param workSchedule объект для сохранения изменений графика работ
     * @return редирект на страницу с графиками работ
     */
    @PostMapping("/save_work_schedule")
    public String saveWorkSchedule(@ModelAttribute("workSchedule") WorkSchedule workSchedule) {
        workScheduleService.save(workSchedule);
        return "redirect:/scheduler/work_schedule";
    }

    /**
     * Удаляет график работ по указанному идентификатору.
     *
     * @param id идентификатор графика работ для удаления
     * @param redirectAttributes атрибуты для отображения сообщения об успехе или ошибке
     * @return редирект на страницу с графиками работ
     */
    @DeleteMapping("/delete_work_schedule/{id}")
    public String deleteWorkSchedule(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            workScheduleService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Запись графика работ с id " + id + " успешно удалена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении записи расписания: " + e.getMessage());
        }
        return "redirect:/scheduler/work_schedule";
    }

    /**
     * Редактирует график работ по указанному идентификатору.
     *
     * @param workSchedule объект для редактирования графика работ
     * @param id идентификатор графика работ для редактирования
     * @return редирект на страницу с графиками работ
     */
    @PostMapping("/edit_work_schedule/{id}")
    public String editWorkSchedule(@ModelAttribute("workSchedule") WorkSchedule workSchedule,
                                   @PathVariable Integer id) {
        workSchedule.setIDRecord(id);
        workScheduleService.edit(workSchedule);

        return "redirect:/scheduler/work_schedule";
    }
}
