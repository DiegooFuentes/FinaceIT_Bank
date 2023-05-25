package com.financeit.web.service;

import com.financeit.web.models.EconomicIndicator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EconomicIndicatorService {

    private final RestTemplate restTemplate;

    public EconomicIndicatorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EconomicIndicator getIndicatorByType(String indicatorType) {
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String url = "https://mindicador.cl/api/" + indicatorType + "/" + todayDate;
        return restTemplate.getForObject(url, EconomicIndicator.class);
    }
}
