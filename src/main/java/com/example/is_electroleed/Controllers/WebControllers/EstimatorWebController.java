package com.example.is_electroleed.Controllers.WebControllers;

import com.example.is_electroleed.entities.DTO.EmployeeDTO;
import com.example.is_electroleed.entities.DTO.ProjectBudgetDTO;
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


/**
 * Контроллер для управления действиями роли "Сметчик" в веб-приложении.
 * Обрабатывает запросы, связанные с функционалом сметчика, включая доступ к данным сотрудников,
 * проектов и смет. Все методы в этом контроллере защищены и доступны только пользователям с ролью
 * "ESTIMATOR". Основной URL для всех маршрутов: /estimator.
 * Зависимости:
 * {@link EmployeeService} для управления данными сотрудников.
 * {@link ProjectService} для работы с данными проектов.
 * {@link EstimateService} для управления данными смет.
 * {@link WorkScheduleService} для работы с расписаниями работ
 */
@Controller
@RequestMapping("/estimator")
@PreAuthorize("hasRole('ESTIMATOR')")
public class EstimatorWebController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EstimateService estimateService;

    @Autowired
    private WorkScheduleService workScheduleService;


    /**
     * Отображает домашнюю страницу для роли "Сметчик".
     * Этот метод проверяет доступ пользователя к ресурсу, извлекает данные о расходах по проектам
     * в виде списка объектов {@link ProjectBudgetDTO} и передает их в представление.
     *
     * @param model объект {@link Model} для передачи данных в представление.
     * @return строка с именем представления "estimator/home", либо перенаправление на другую страницу
     * в случае отсутствия доступа.
     */
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("totalProjects", projectService.getTotalProjects());
        model.addAttribute("activeProjectsCount", projectService.getActiveProjectsCount());
        model.addAttribute("completedProjectsCount", projectService.getCompletedProjectsCount());
        List<ProjectBudgetDTO> projectBudgets = estimateService.getSpentBudgetByProjects();
        model.addAttribute("projectBudgets", projectBudgets);
        return "estimator/home";
    }

    /**
     * Отображает список смет с возможностью поиска и сортировки.
     *
     * @param model   объект {@link Model} для передачи данных в представление.
     * @param keyword ключевое слово для поиска смет.
     * @param sort    параметр сортировки (например, "IDProject" или "recordDate").
     * @param order   порядок сортировки ("asc" или "desc").
     * @return строка с именем представления "admin/estimate".
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
        return "estimator/estimate";
    }

    /**
     * Создает новую запись сметы, привязанную к проекту.
     *
     * @param IDProject идентификатор проекта, к которому привязывается смета.
     * @param estimate  объект {@link Estimate}, содержащий данные новой сметы.
     * @param model     объект {@link Model} для передачи данных в представление.
     * @return перенаправление на список смет после успешного добавления.
     */
    @PostMapping("/new_estimate")
    public String newEstimate(@RequestParam Integer IDProject,
                              @ModelAttribute("estimate") Estimate estimate,
                              Model model) {
        Project project = projectService.findById(IDProject);
        if (project == null) {
            model.addAttribute("error", "Проект с данным индексом не найден.");
            return "redirect:/estimator/estimate";
        }

        estimate.setIDProject(project);
        estimateService.save(estimate);
        return "redirect:/admin/estimate";
    }

    /**
     * Сохраняет изменения в записи сметы.
     *
     * @param estimate объект {@link Estimate}, содержащий обновленные данные сметы.
     * @return перенаправление на список смет после успешного сохранения.
     */
    @PostMapping("/save_estimate")
    public String saveEstimate(@ModelAttribute("estimate") Estimate estimate) {
        estimateService.save(estimate);
        return "redirect:/estimator/estimate";
    }

    /**
     * Удаляет запись сметы по ее идентификатору.
     *
     * @param id                  идентификатор удаляемой записи сметы.
     * @param redirectAttributes  объект {@link RedirectAttributes} для передачи сообщений.
     * @return перенаправление на список смет после успешного удаления.
     */
    @DeleteMapping("/delete_estimate/{id}")
    public String deleteEstimate(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            estimateService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Запись сметы с id " + id + " успешно удалена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении записи сметы: " + e.getMessage());
        }
        return "redirect:/estimator/estimate";
    }

    /**
     * Редактирует существующую запись сметы.
     *
     * @param estimate объект {@link Estimate}, содержащий обновленные данные.
     * @param id       идентификатор редактируемой записи сметы.
     * @return перенаправление на список смет после успешного редактирования.
     */
    @PostMapping("/edit_estimate/{id}")
    public String editEstimate(@ModelAttribute("estimate") Estimate estimate,
                               @PathVariable Integer id) {

        estimate.setIDRecord(id);
        estimateService.edit(estimate);

        return "redirect:/estimator/estimate";
    }

    /**
     * Отображает список сотрудников с возможностью поиска и сортировки.
     *
     * @param model   объект {@link Model} для передачи данных в представление.
     * @param keyword ключевое слово для поиска сотрудников.
     * @param sort    параметр сортировки ("login" или "position").
     * @param order   порядок сортировки ("asc" или "desc").
     * @return строка с именем представления "estimator/employee".
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
        return "estimator/employee";
    }

    /**
     * Отображает список проектов с возможностью поиска и сортировки.
     *
     * @param model   объект {@link Model} для передачи данных в представление.
     * @param keyword ключевое слово для поиска проектов.
     * @param sort    параметр сортировки (например, "startDate" или "endDate").
     * @param order   порядок сортировки ("asc" или "desc").
     * @return строка с именем представления "admin/project".
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
        return "estimator/project";
    }

    /**
     * Отображает список графика работ с возможностью поиска и сортировки.
     *
     * @param model   объект {@link Model} для передачи данных в представление.
     * @param keyword ключевое слово для поиска записи графика работ.
     * @param sort    параметр сортировки (например, "startDate" или "endDate").
     * @param order   порядок сортировки ("asc" или "desc").
     * @return строка с именем представления "admin/work_schedule".
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
        return "estimator/work_schedule";
    }
}
