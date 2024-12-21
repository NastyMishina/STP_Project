package com.example.is_electroleed.Controllers.WebControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/auth")
public class AuthWebController {

    /**
     * Отображает HTML-страницу авторизации.
     *
     * @return имя HTML-шаблона.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/about_author")
    public String showAboutAuthorPage() {
        return "auth/about_author";
    }
}
