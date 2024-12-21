package com.example.is_electroleed.Controllers.WebControllers;

import com.example.is_electroleed.entities.DTO.EmployeeDTO;
import com.example.is_electroleed.entities.Estimate;
import com.example.is_electroleed.entities.Project;
import com.example.is_electroleed.entities.WorkSchedule;
import com.example.is_electroleed.service.EmployeeService;
import com.example.is_electroleed.service.EstimateService;
import com.example.is_electroleed.service.ProjectService;
import com.example.is_electroleed.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления действиями роли "Менеджер проектов" в веб-приложении.
 *
 * <p>Обрабатывает запросы, связанные с управлением проектами, сотрудниками, сметами и расписаниями работ.
 * Все методы в этом контроллере защищены и доступны только пользователям с ролью "PROJECT_MANAGER".</p>
 *
 * <p>Основной URL для всех маршрутов: <code>/project_manager</code>.</p>
 *
 * <p>Зависимости:</p>
 * <ul>
 *     <li>{@link EmployeeService} - управление данными сотрудников.</li>
 *     <li>{@link ProjectService} - управление данными проектов.</li>
 *     <li>{@link EstimateService} - управление данными смет.</li>
 *     <li>{@link WorkScheduleService} - управление расписаниями работ.</li>
 * </ul>
 *
 *
 * <p>Ключевые маршруты:</p>
 * <ul>
 *     <li><b>/home</b> - отображение главной страницы менеджера проектов.</li>
 *     <li><b>/employee</b> - управление списком сотрудников.</li>
 *     <li><b>/project</b> - управление списком проектов.</li>
 *     <li><b>/estimate</b> - управление данными смет.</li>
 *     <li><b>/work_schedule</b> - управление расписаниями работ.</li>
 * </ul>
 *
 * @see PreAuthorize
 */
@Controller
@RequestMapping("/project_manager")
@PreAuthorize("hasRole('PROJECT_MANAGER')")
public class ProjectManagerWebController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EstimateService estimateService;

    @Autowired
    private WorkScheduleService workScheduleService;


    /**
     * Отображает главную страницу для роли "Менеджер проектов".
     * Передает в модель:
     * Общее количество проектов.
     * Количество активных и завершенных проектов.
     * Статистику задач по проектам и статусам.
     *
     * @param model Модель для передачи данных в представление.
     * @return Имя представления: project_manager/home.
     */
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("totalProjects", projectService.getTotalProjects());
        model.addAttribute("activeProjectsCount", projectService.getActiveProjectsCount());
        model.addAttribute("completedProjectsCount", projectService.getCompletedProjectsCount());

        Map<String, Map<String, Long>> taskCounts = workScheduleService.getTaskCountsByProjectAndStatus();
        model.addAttribute("taskCounts", taskCounts);
        return "project_manager/home";
    }

    /**
     * Отображает список сотрудников с возможностью фильтрации и сортировки по полям "position" и "login".
     *
     * @param model модель для передачи данных на представление
     * @param keyword ключевое слово для поиска сотрудников
     * @param sort поле для сортировки
     * @param order порядок сортировки
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
        return "project_manager/employee";
    }

    /**
     * Отображает список проектов с возможностью фильтрации и сортировки по полям "startDate" и "endDate".
     *
     * @param model модель для передачи данных на представление
     * @param keyword ключевое слово для поиска проектов
     * @param sort поле для сортировки
     * @param order порядок сортировки
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
        return "project_manager/project";
    }

    /**
     * Создает новый проект.
     *
     * @param project объект проекта для создания
     * @return перенаправление на страницу списка проектов
     */
    @PostMapping("/new_project")
    public String newProject(@ModelAttribute("project") Project project) {
        projectService.save(project);
        return "redirect:/project_manager/project";
    }

    /**
     * Сохраняет изменения в проекте.
     *
     * @param project объект проекта с изменениями
     * @return перенаправление на страницу списка проектов
     */
    @PostMapping("/save_project")
    public String saveProject(@ModelAttribute("project") Project project) {
        projectService.save(project);
        return "redirect:/project_manager/project";

    }

    /**
     * Удаляет проект по заданному id.
     *
     * @param model модель для передачи данных на представление
     * @param id идентификатор проекта для удаления
     * @param redirectAttributes атрибуты для передачи сообщения об успешной операции
     * @return перенаправление на страницу списка проектов с сообщением об успехе или ошибке
     */
    @DeleteMapping("/delete_project/{id}")
    public String deleteProject(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            projectService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Проект с id" + id + " успешно удален.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении проекта: " + e.getMessage());
        }
        return "redirect:/project_manager/project";
    }

    /**
     * Редактирует проект с заданным id.
     *
     * @param project объект проекта с новыми данными
     * @param id идентификатор проекта для редактирования
     * @return перенаправление на страницу списка проектов
     */
    @PostMapping("/edit_project/{id}")
    public String editProject(@ModelAttribute("project") Project project,
                              @PathVariable Integer id) {
        project.setIDProject(id);
        projectService.edit(project);

        return "redirect:/project_manager/project";
    }

    /**
     * Отображает записей список смет с возможностью фильтрации и сортировки.
     *
     * @param model модель для передачи данных на представление
     * @param keyword ключевое слово для поиска смет
     * @param sort поле для сортировки
     * @param order порядок сортировки
     * @return имя представления для отображения списка смет
     */
    @GetMapping("/estimate")
    public String viewEstimate(Model model, @Param("keyword") String keyword,
                               @RequestParam(required = false) String sort,
                               @RequestParam(required = false) String order) {
        List<Estimate> listEstimates;
        if ("IDProject".equals(sort)) {
            listEstimates = "desc".equals(order) ? estimateService.findAllSortedByIDProjectDesc() :
                    estimateService.findAllSortedByIDProjectAsc();
        } else if ("recordDate".equals(sort)) {
            listEstimates = "desc".equals(order) ? estimateService.findAllSortedByRecordDateDesc() :
                    estimateService.findAllSortedByRecordDateAsc();
        } else {
            listEstimates = estimateService.getAllEstimates(keyword);
        }

        model.addAttribute("listEstimates", listEstimates);
        model.addAttribute("keyword", keyword);
        return "project_manager/estimate";
    }

    /**
     * Создает новую смету для указанного проекта.
     *
     * @param IDProject идентификатор проекта для привязки сметы
     * @param estimate объект сметы для создания
     * @param model модель для передачи данных на представление
     * @return перенаправление на страницу списка смет
     */
    @PostMapping("/new_estimate")
    public String newEstimate(@RequestParam Integer IDProject,
                              @ModelAttribute("estimate") Estimate estimate,
                              Model model) {
        Project project = projectService.findById(IDProject);
        if (project == null) {
            model.addAttribute("error", "Проект с данным индексом не найден.");
            return "redirect:/project_manager/estimate";
        }

        estimate.setIDProject(project);
        estimateService.save(estimate);
        return "redirect:/project_manager/estimate";
    }

    /**
     * Сохраняет изменения в смете.
     *
     * @param estimate объект сметы с изменениями
     * @return перенаправление на страницу списка смет
     */
    @PostMapping("/save_estimate")
    public String saveEstimate(@ModelAttribute("estimate") Estimate estimate) {
        estimateService.save(estimate);
        return "redirect:/project_manager/estimate";
    }

    /**
     * Удаляет смету по заданному id.
     *
     * @param id идентификатор сметы для удаления
     * @param redirectAttributes атрибуты для передачи сообщения об успешной операции
     * @return перенаправление на страницу списка смет с сообщением об успехе или ошибке
     */
    @DeleteMapping("/delete_estimate/{id}")
    public String deleteEstimate(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            estimateService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Запись сметы с id " + id + " успешно удалена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении записи сметы: " + e.getMessage());
        }
        return "redirect:/project_manager/estimate";
    }

    /**
     * Редактирует запись в расписании с заданным id.
     *
     * @param estimate объект расписания с новыми данными
     * @param id идентификатор записи расписания для редактирования
     * @return перенаправление на страницу списка расписаний
     */
    @PostMapping("/edit_estimate/{id}")
    public String editEstimate(@ModelAttribute("estimate") Estimate estimate,
                               @PathVariable Integer id) {
        estimate.setIDRecord(id);
        estimateService.edit(estimate);

        return "redirect:/project_manager/estimate";
    }

    /**
     * Отображает список графика работ с возможностью фильтрации и сортировки по полям "startDate" и "endDate".
     *
     * @param model модель для передачи данных на представление
     * @param keyword ключевое слово для поиска графика работ
     * @param sort поле для сортировки
     * @param order порядок сортировки
     * @return имя представления для отображения списка графика работ
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
        return "project_manager/work_schedule";
    }

    /**
     * Создает новую запись графика работ.
     *
     * @param workSchedule объект записи графика работ для создания
     * @return перенаправление на страницу списка графика работ
     */
    @PostMapping("/new_work_schedule")
    public String newWorkSchedule(@ModelAttribute("workSchedule") WorkSchedule workSchedule) {
        workScheduleService.save(workSchedule);
        return "redirect:/project_manager/work_schedule";
    }

    /**
     * Сохраняет изменения в графике работ.
     *
     * @param workSchedule объект графика работ с изменениями
     * @return перенаправление на страницу списка графиков работ
     */
    @PostMapping("/save_work_schedule")
    public String saveWorkSchedule(@ModelAttribute("workSchedule") WorkSchedule workSchedule) {
        workScheduleService.save(workSchedule);
        return "redirect:/project_manager/work_schedule";
    }

    /**
     * Удаляет запись из графика работ по заданному id.
     *
     * @param id идентификатор записи графика работ для удаления
     * @param redirectAttributes атрибуты для передачи сообщения об успешной операции
     * @return перенаправление на страницу списка графиков работ с сообщением об успехе или ошибке
     */
    @DeleteMapping("/delete_work_schedule/{id}")
    public String deleteWorkSchedule(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            workScheduleService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Запись расписания с id " + id + " успешно удалена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении записи расписания: " + e.getMessage());
        }
        return "redirect:/project_manager/work_schedule";
    }

    /**
     * Редактирует запись в графике работ с заданным id.
     *
     * @param workSchedule объект графика работ с новыми данными
     * @param id идентификатор записи графика работ для редактирования
     * @return перенаправление на страницу списка графиков работ
     */
    @PostMapping("/edit_work_schedule/{id}")
    public String editWorkSchedule(                                   @ModelAttribute("workSchedule") WorkSchedule workSchedule,
                                   @PathVariable Integer id) {
        workSchedule.setIDRecord(id);
        workScheduleService.edit(workSchedule);

        return "redirect:/project_manager/work_schedule";
    }
}
