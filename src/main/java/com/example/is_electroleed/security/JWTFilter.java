package com.example.is_electroleed.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для обработки JWT (JSON Web Token) аутентификации. Этот фильтр проверяет наличие токена в cookies запроса,
 * затем извлекает информацию о пользователе, если токен действителен, и устанавливает аутентификацию в контексте безопасности.
 * JWT токен используется для аутентификации и авторизации пользователя в системе.
 * Фильтр обрабатывает запросы один раз (OncePerRequestFilter) для обеспечения безопасности всех входящих запросов.
 */
@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private CompanyUserDetailsService userDetailsService;

    @Autowired
    private UtilJWT utilJwt;

    /**
     * Получает значение cookie по имени.
     * Метод ищет cookie с указанным именем и возвращает его значение, если оно существует.
     * Если cookie с таким именем не найдено, возвращается null.
     *
     * @param request HTTP запрос, содержащий cookies.
     * @param name Имя cookie, которое нужно найти.
     * @return Значение cookie, если найдено, иначе null.
     */
    public String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Метод, выполняющий фильтрацию и установку аутентификации для запроса.
     * Если токен JWT присутствует в cookies и является действительным, фильтр извлекает информацию из токена,
     * загружает данные пользователя, устанавливает аутентификацию в контексте безопасности.
     * Если токен неверный или отсутствует, возвращается ошибка с кодом SC_BAD_REQUEST.
     *
     * @param request HTTP запрос, который обрабатывается фильтром.
     * @param response HTTP ответ, который отправляется клиенту.
     * @param filterChain Цепочка фильтров для дальнейшей обработки запроса.
     * @throws ServletException Если возникает ошибка при обработке запроса.
     * @throws IOException Если возникает ошибка ввода-вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Извлекаем JWT из cookies запроса
        String jwt = getCookie(request, "jwt-token");
        if (jwt != null && !jwt.isBlank()) {
            try {
                // Проверка токена и извлечение данных
                TokenInfo tokenInfo = utilJwt.verifyToken(jwt);
                // Загрузка данных пользователя на основе информации из токена
                UserDetails userDetails = userDetailsService.loadUserByUsername(tokenInfo.getLogin());
                // Создание аутентификационного токена
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(tokenInfo.getLogin(), userDetails.getPassword(), userDetails.getAuthorities());
                // Устанавливаем аутентификацию в SecurityContext, если она ещё не установлен
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (JWTVerificationException exc) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token");
            }
        }
        filterChain.doFilter(request, response);
    }
}
