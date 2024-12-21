package com.example.is_electroleed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурационный класс для настройки различных компонентов приложения.
 * Реализует интерфейс {@link WebMvcConfigurer} для дополнительной настройки веб-контекста.
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    /**
     * Создает и настраивает {@link RestTemplate} для выполнения HTTP-запросов.
     *
     * @return объект {@link RestTemplate}, который может быть использован для взаимодействия с RESTful сервисами.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Создает и настраивает фильтр {@link HiddenHttpMethodFilter} для обработки скрытых HTTP-методов (например, PUT и DELETE),
     * которые могут быть отправлены через формы в HTML.
     *
     * @return объект {@link HiddenHttpMethodFilter}, необходимый для корректной обработки скрытых методов.
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
