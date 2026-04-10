package org.mainapp;

import org.mainapp.service.MatchesService;
import org.mainapp.service.TeamsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableCaching
public class MainAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(MatchesService matchesService, TeamsService teamsService)
    {
        return args -> {
            teamsService.initializeData();
            matchesService.initializeData();
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://127.0.0.1:5500")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

}


//TODO dorobienie symulacji na zasadzie dane sa przesylane tylko wtedy gdy zmieni sie wynik lub skonczy sie mecz
//TODO aktualizacja bazy danych po skonczonej symulacji
//TODO odtworzenie meczu ( logow ) z kafki

//TODO poprawienie backu - walidacja czy mecz ma dobry status,