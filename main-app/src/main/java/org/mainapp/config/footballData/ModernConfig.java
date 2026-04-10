package org.mainapp.config.footballData;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "football" , types = {MatchesClientService.class, TeamsClientService.class})
public class ModernConfig {

    @Value("${my.secret.key}")
    private String key;

    @Bean
    RestClientHttpServiceGroupConfigurer multipleGroups()
    {
        return groups -> {
            groups.filterByName("football")
                    .forEachClient((group,builder) -> builder
                            .baseUrl("https://api.football-data.org/v4")
                            .defaultHeader("X-Auth-Token", key)
                            .build());
        };
    }
}
