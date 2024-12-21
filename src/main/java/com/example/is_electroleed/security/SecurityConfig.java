package com.example.is_electroleed.security;

import com.example.is_electroleed.repositories.UserRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Конфигурация безопасности приложения, настроенная с использованием Spring Security.
 * Этот класс управляет настройками безопасности для всей системы, включая авторизацию, аутентификацию, управление сессиями и защиту от CSRF атак.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JWTFilter filter;

    @Autowired
    private CompanyUserDetailsService userService;

    /**
     * Конфигурирует фильтрацию запросов с помощью Spring Security.
     * Настроены URL-адреса, роли пользователей, аутентификация.
     *
     * @param http объект HttpSecurity для настройки параметров безопасности
     * @return настроенная SecurityFilterChain для безопасности
     * @throws Exception если возникает ошибка при настройке безопасности
     */
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/icons/**").permitAll()
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/web/auth/login").permitAll()
                .requestMatchers("/web/auth/about_author").permitAll()
                .requestMatchers("/auth/register").permitAll()
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/estimator/**").hasRole("ESTIMATOR")
                .requestMatchers("/scheduler/**").hasRole("SCHEDULER")
                .requestMatchers("/project_manager/**").hasRole("PROJECT_MANAGER")
                .requestMatchers("/project_member/**").hasRole("PROJECT_MEMBER"))
                .userDetailsService(userService)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                    )
                )
                .sessionManagement(sessionManagement -> sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Бин для настройки провайдера аутентификации.
     * Устанавливает сервис для получения данных пользователя и шифрования пароля.
     *
     * @param userDetailsService сервис для загрузки данных пользователя
     * @param passwordEncoder кодировщик пароля
     * @return настроенный AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(CompanyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    /**
     * Бин для создания и настройки кодировщика паролей с использованием BCrypt.
     *
     * @return кодировщик паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Бин для создания и настройки менеджера аутентификации.
     * Устанавливает сервис для загрузки пользователя и кодировщик паролей.
     *
     * @param http объект HttpSecurity для получения AuthenticationManagerBuilder
     * @return настроенный AuthenticationManager
     * @throws Exception если возникает ошибка при настройке менеджера аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}