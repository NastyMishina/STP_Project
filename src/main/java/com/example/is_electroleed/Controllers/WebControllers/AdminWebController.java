package com.example.is_electroleed.Controllers.WebControllers;

import com.example.is_electroleed.entities.*;
import com.example.is_electroleed.repositories.UserRepo;
import com.example.is_electroleed.security.UtilJWT;
import com.example.is_electroleed.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Контроллер для управления административными функциями веб-приложения.
 * Предоставляет методы для работы с пользователями, сотрудниками, проектами,
 * сметами и расписаниями. Доступен только пользователям с ролью "ADMIN"
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWebController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EstimateService estimateService;

    @Autowired
    private WorkScheduleService workScheduleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UtilJWT jwtUtil;

    /**
     * Отображает HTML-страницу регистрации.
     *
     * @return имя HTML-шаблона.
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "admin/register";
    }

    /**
     * Обработчик для регистрации нового пользователя.
     *
     * @param user данные пользователя для регистрации
     * @return ResponseEntity с JWT токеном или ошибкой
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerHandler(@RequestBody User user) {
        try {
            // Создание нового пользователя с зашифрованным паролем
            User newUser = new User();
            newUser.setLogin(user.getLogin());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newUser.setRole(user.getRole());

            // Сохранение нового пользователя в базе данных
            userRepo.save(newUser);

            // Генерация JWT токена для нового пользователя
            String token = jwtUtil.generateToken(newUser.getLogin(), newUser.getPassword());

            // Возвращение токена в ответе
            return ResponseEntity.ok(Collections.singletonMap("jwt-token", token));
        } catch (DataIntegrityViolationException ex) {
            // Обработка ошибки, если логин уже существует
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", "Пользователь с таким логином уже существует"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Произошла ошибка при регистрации пользователя"));
        }
    }

    /**
     * Проверяет доступ пользователя к административным страницам.
     * Добавляет имя пользователя и его роли в модель, если доступ разрешен.
     *
     * @param model объект для передачи данных в представление.
     * @return null - если доступ разрешен, /auth/login - если доступа нет.
     */
    public String checkAccess(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();

            model.addAttribute("username", username);
            model.addAttribute("roles", roles);

            return null;
        }
        return "redirect:/auth/login";
    }

    /**
     * Отображает главную страницу панели администратора.
     *
     * @param model объект для передачи данных в представление.
     * @return имя представления или адрес перенаправления при отсутствии доступа.
     */
    @GetMapping("/home")
    public String home(Model model) {
        String url = checkAccess(model);
        return Objects.requireNonNullElse(url, "admin/home");
    }

    /**
     * Отображает список пользователей с возможностью поиска и сортировки.
     *
     * @param model  объект для передачи данных в представление.
     * @param keyword ключевое слово для поиска.
     * @param sort    параметр сортировки (например, "login" или "role").
     * @param order   порядок сортировки ("asc" или "desc").
     * @return имя представления или адрес перенаправления при отсутствии доступа.
     */
    @GetMapping("/user")
    public String viewUsers(Model model, @Param("keyword") String keyword,
                            @RequestParam(required = false) String sort,
                            @RequestParam(required = false) String order) {

        List<User> listUsers;
        if ("login".equals(sort)) {
            listUsers = "desc".equals(order) ? userService.findAllSortedByLoginDesc() :
                    userService.findAllSortedByLoginAsc();
        } else if ("role".equals(sort)) {
            listUsers = "desc".equals(order) ? userService.findAllSortedByRoleDesc() :
                    userService.findAllSortedByRoleAsc();
        } else {
            listUsers = userService.getAllUsers(keyword);
        }
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("keyword", keyword);
        return "admin/user";
    }

    /**
     * Сохраняет нового пользователя.
     *
     * @param user  объект пользователя.
     * @param model объект для передачи данных в представление.
     * @return адрес перенаправления.
     */
    @PostMapping("/new_user")
    public String newUser(@ModelAttribute("user") User user, Model model) {
        userService.save(user);
        return "redirect:/admin/user";
    }

    /**
     * Сохраняет пользователя.
     *
     * @param model объект для передачи данных в представление.
     * @param user  объект пользователя.
     * @return адрес перенаправления на страницу таблицы.
     */
    @PostMapping("/save")
    public String saveUser(Model model, @ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/admin/user";

    }

    /**
     * Удаляет пользователя по логину.
     *
     * @param model              объект для передачи данных в представление.
     * @param login              логин пользователя.
     * @param redirectAttributes объект для передачи сообщений между запросами.
     * @return адрес перенаправления.
     */
    @DeleteMapping("/delete/{login}")
    public String deleteUser(Model model, @PathVariable String login, RedirectAttributes redirectAttributes) {
        try {
        userService.deleteByLogin(login);
            redirectAttributes.addFlashAttribute("message", "Пользователь с логином " + login + " успешно удален.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении пользователя: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }

    /**
     * Редактирует существующую запись в системе.
     * Обновляет данные сущности в соответствии с переданным объектом.
     *
     * @param model   объект, используемый для передачи данных в представление.
     * @param id      идентификатор редактируемой сущности.
     * @return строка с перенаправлением на список сущностей после успешного редактирования.
     */
    @PostMapping("/edit/{id}")
    public String editUser(Model model,
                           @PathVariable Integer id) {
        User user = userService.findById(id);
        userService.edit(user);

        return "redirect:/admin/user";
    }

    /**
     * Отображает список сотрудников с возможностью поиска и сортировки.
     *
     * @param model   объект {@link Model} для передачи данных в представление.
     * @param keyword ключевое слово для поиска сотрудников.
     * @param sort    параметр сортировки (например, "full_name" или "position").
     * @param order   порядок сортировки ("asc" или "desc").
     * @return строка с именем представления "admin/employee".
     */
    @GetMapping("/employee")
    public String viewEmpl(Model model, @Param("keyword") String keyword,
                            @RequestParam(required = false) String sort,
                            @RequestParam(required = false) String order) {
        List<Employee> listEmployees;
        if ("full_name".equals(sort)) {
            listEmployees = "desc".equals(order) ? employeeService.findAllSortedByFullNameDesc() :
                    employeeService.findAllSortedByFullNameAsc();
        } else if ("position".equals(sort)) {
            listEmployees = "desc".equals(order) ? employeeService.findAllSortedByPositionDesc() :
                    employeeService.findAllSortedByPositionAsc();
        } else {
            listEmployees = employeeService.getAllEmployees(keyword);
        }
        model.addAttribute("listEmployees", listEmployees);
        model.addAttribute("keyword", keyword);
        return "admin/employee";
    }

    /**
     * Создает нового сотрудника.
     *
     * @param employee объект {@link Employee}, содержащий данные нового сотрудника.
     * @param model    объект {@link Model} для передачи данных в представление.
     * @return перенаправление на список сотрудников после успешного добавления.
     */
    @PostMapping("/new_employee")
    public String newEmployee(@ModelAttribute("employee") Employee employee, Model model) {
        employeeService.save(employee);
        return "redirect:/admin/employee";
    }

    /**
     * Сохраняет изменения в информации о сотруднике.
     *
     * @param model    объект {@link Model} для передачи данных в представление.
     * @param employee объект {@link Employee}, содержащий обновленные данные сотрудника.
     * @return перенаправление на список сотрудников после успешного сохранения.
     */
    @PostMapping("/save_employee")
    public String saveEmployee(Model model, @ModelAttribute("employee") Employee employee) {
        employeeService.save(employee);
        return "redirect:/admin/employee";

    }

    /**
     * Удаляет сотрудника по его идентификатору.
     *
     * @param model               объект {@link Model} для передачи данных в представление.
     * @param id                  идентификатор удаляемого сотрудника.
     * @param redirectAttributes  объект {@link RedirectAttributes} для передачи сообщений.
     * @return перенаправление на список сотрудников после успешного удаления.
     */
    @DeleteMapping("/delete_employee/{id}")
    public String deleteEmployee(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Сотрудник с id" + id + " успешно удален.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении сотрудника: " + e.getMessage());
        }
        return "redirect:/admin/employee";
    }

    /**
     * Редактирует существующего сотрудника.
     *
     * @param model    объект {@link Model} для передачи данных в представление.
     * @param employee объект {@link Employee}, содержащий обновленные данные.
     * @param id       идентификатор редактируемого сотрудника.
     * @return перенаправление на список сотрудников после успешного редактирования.
     */
    @PostMapping("/edit_employee/{id}")
    public String editEmployee(Model model,
                               @ModelAttribute("employee") Employee employee,
                               @PathVariable Integer id) {
        employee.setIDEmployee(id);
        employeeService.edit(employee);

        return "redirect:/admin/employee";
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
        return "admin/project";
    }

    /**
     * Создает новый проект.
     *
     * @param project объект {@link Project}, содержащий данные нового проекта.
     * @param model   объект {@link Model} для передачи данных в представление.
     * @return перенаправление на список проектов после успешного добавления.
     */
    @PostMapping("/new_project")
    public String newProject(@ModelAttribute("project") Project project, Model model) {
        projectService.save(project);
        return "redirect:/admin/project";
    }

    /**
     * Сохраняет изменения в информации о проекте.
     *
     * @param model   объект {@link Model} для передачи данных в представление.
     * @param project объект {@link Project}, содержащий обновленные данные проекта.
     * @return перенаправление на список проектов после успешного сохранения.
     */
    @PostMapping("/save_project")
    public String saveProject(Model model, @ModelAttribute("project") Project project) {
        projectService.save(project);
        return "redirect:/admin/project";

    }

    /**
     * Удаляет проект по его идентификатору.
     *
     * @param model               объект {@link Model} для передачи данных в представление.
     * @param id                  идентификатор удаляемого проекта.
     * @param redirectAttributes  объект {@link RedirectAttributes} для передачи сообщений.
     * @return перенаправление на список проектов после успешного удаления.
     */
    @DeleteMapping("/delete_project/{id}")
    public String deleteProject(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            projectService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Проект с id" + id + " успешно удален.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении проекта: " + e.getMessage());
        }
        return "redirect:/admin/project";
    }

    /**
     * Редактирует существующий проект.
     *
     * @param model   объект {@link Model} для передачи данных в представление.
     * @param project объект {@link Project}, содержащий обновленные данные.
     * @param id      идентификатор редактируемого проекта.
     * @return перенаправление на список проектов после успешного редактирования.
     */
    @PostMapping("/edit_project/{id}")
    public String editProject(Model model,
                               @ModelAttribute("project") Project project,
                               @PathVariable Integer id) {
                project.setIDProject(id);
        projectService.edit(project);

        return "redirect:/admin/project";
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
        return "admin/estimate";
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
            return "redirect:/admin/estimate";
        }

        estimate.setIDProject(project);
        estimateService.save(estimate);
        return "redirect:/admin/estimate";
    }

    /**
     * Сохраняет изменения в записи сметы.
     *
     * @param model    объект {@link Model} для передачи данных в представление.
     * @param estimate объект {@link Estimate}, содержащий обновленные данные сметы.
     * @return перенаправление на список смет после успешного сохранения.
     */
    @PostMapping("/save_estimate")
    public String saveEstimate(Model model, @ModelAttribute("estimate") Estimate estimate) {
        estimateService.save(estimate);
        return "redirect:/admin/estimate";
    }

    /**
     * Удаляет запись сметы по ее идентификатору.
     *
     * @param model               объект {@link Model} для передачи данных в представление.
     * @param id                  идентификатор удаляемой записи сметы.
     * @param redirectAttributes  объект {@link RedirectAttributes} для передачи сообщений.
     * @return перенаправление на список смет после успешного удаления.
     */
    @DeleteMapping("/delete_estimate/{id}")
    public String deleteEstimate(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            estimateService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Запись сметы с id " + id + " успешно удалена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении записи сметы: " + e.getMessage());
        }
        return "redirect:/admin/estimate";
    }

    /**
     * Редактирует существующую запись сметы.
     *
     * @param model    объект {@link Model} для передачи данных в представление.
     * @param estimate объект {@link Estimate}, содержащий обновленные данные.
     * @param id       идентификатор редактируемой записи сметы.
     * @return перенаправление на список смет после успешного редактирования.
     */
    @PostMapping("/edit_estimate/{id}")
    public String editEstimate(Model model,
                               @ModelAttribute("estimate") Estimate estimate,
                               @PathVariable Integer id) {
        estimate.setIDRecord(id);
        estimateService.edit(estimate);

        return "redirect:/admin/estimate";
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
        return "admin/work_schedule";
    }

    /**
     * Создает новую запись графика работ.
     *
     * @param workSchedule объект {@link WorkSchedule}, содержащий данные нового графика работ.
     * @param model        объект {@link Model} для передачи данных в представление.
     * @return перенаправление на список графика работ после успешного добавления.
     */
    @PostMapping("/new_work_schedule")
    public String newWorkSchedule(@ModelAttribute("workSchedule") WorkSchedule workSchedule, Model model) {
        workScheduleService.save(workSchedule);
        return "redirect:/admin/work_schedule";
    }

    /**
     * Сохраняет изменения в записи графика работ.
     *
     * @param model        объект {@link Model} для передачи данных в представление.
     * @param workSchedule объект {@link WorkSchedule}, содержащий обновленные данные графика работ.
     * @return перенаправление на список графика работ после успешного сохранения.
     */
    @PostMapping("/save_work_schedule")
    public String saveWorkSchedule(Model model, @ModelAttribute("workSchedule") WorkSchedule workSchedule) {
        workScheduleService.save(workSchedule);
        return "redirect:/admin/work_schedule";
    }

    /**
     * Удаляет запись графика работ по ее идентификатору.
     *
     * @param model               объект {@link Model} для передачи данных в представление.
     * @param id                  идентификатор удаляемой записи графика работ.
     * @param redirectAttributes  объект {@link RedirectAttributes} для передачи сообщений.
     * @return перенаправление на список графика работ после успешного удаления.
     */
    @DeleteMapping("/delete_work_schedule/{id}")
    public String deleteWorkSchedule(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            workScheduleService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Запись расписания с id " + id + " успешно удалена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении записи расписания: " + e.getMessage());
        }
        return "redirect:/admin/work_schedule";
    }

    /**
     * Редактирует существующую запись графика работ.
     *
     * @param model        объект {@link Model} для передачи данных в представление.
     * @param workSchedule объект {@link WorkSchedule}, содержащий обновленные данные.
     * @param id           идентификатор редактируемой записи расписания.
     * @return перенаправление на список графика работ после успешного редактирования.
     */
    @PostMapping("/edit_work_schedule/{id}")
    public String editWorkSchedule(Model model,
                                   @ModelAttribute("workSchedule") WorkSchedule workSchedule,
                                   @PathVariable Integer id) {
        workSchedule.setIDRecord(id);
        workScheduleService.edit(workSchedule);

        return "redirect:/admin/work_schedule";
    }
}
