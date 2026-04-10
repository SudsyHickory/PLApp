package org.mainapp.config.footballData;


import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/competitions/2021/matches")
public interface MatchesClientService {

    @GetExchange
    MatchesResponse findAll();
}
