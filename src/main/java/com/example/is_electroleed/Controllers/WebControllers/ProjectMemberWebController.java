package com.example.is_electroleed.Controllers.WebControllers;

import com.example.is_electroleed.entities.DTO.EmployeeDTO;
import com.example.is_electroleed.entities.Project;
import com.example.is_electroleed.service.EmployeeService;
import com.example.is_electroleed.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;


/**
 * Контроллер для управления веб-интерфейсом для участников проектов.
 * Контроллер обрабатывает маршруты, доступные для участников проекта (с ролью `PROJECT_MEMBER`).
 * Он предоставляет функциональность для отображения информации о проектах, сотрудниках и домашней странице участника.
 * Все методы в данном классе защищены аннотацией `@PreAuthorize`,
 * которая проверяет роль пользователя перед доступом к ресурсам.
 * Маршруты:
 * - `/home` — домашняя страница участника проекта, где отображается статистика по проектам.
 * - `/employee` — список сотрудников, с возможностью фильтрации и сортировки по логину или должности.
 * - `/project` — список проектов, с возможностью фильтрации и сортировки по дате начала и окончания.
 * Каждое действие сначала выполняет проверку доступа с помощью метода `checkAccess`, чтобы убедиться,
 * что пользователь авторизован и имеет нужные права.
 * Используемые сервисы:
 * - `ProjectService` — сервис для работы с проектами.
 * - `EmployeeService` — сервис для работы с сотрудниками.
 */
@Controller
@RequestMapping("/project_member")
@PreAuthorize("hasRole('PROJECT_MEMBER')")
public class ProjectMemberWebController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * Отображает домашнюю страницу для участника проекта.
     *
     * @param model модель для передачи данных на представление
     * @return имя представления для отображения домашней страницы
     */
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("totalProjects", projectService.getTotalProjects());
        model.addAttribute("activeProjectsCount", projectService.getActiveProjectsCount());
        model.addAttribute("completedProjectsCount", projectService.getCompletedProjectsCount());

        return "project_member/home";
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
}
