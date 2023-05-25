package com.financeit.web.controllers;

import com.financeit.web.models.EconomicIndicator;
import com.financeit.web.service.EconomicIndicatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EconomicIndicatorController {
    private final EconomicIndicatorService indicatorService;

    public EconomicIndicatorController(EconomicIndicatorService indicatorService) {
        this.indicatorService = indicatorService;
    }

    @GetMapping("indicator/{indicatorType}")
    public ResponseEntity<EconomicIndicator> getIndicator(@PathVariable String indicatorType) {
        EconomicIndicator indicator = indicatorService.getIndicatorByType(indicatorType);
        if (indicator != null) {
            return ResponseEntity.ok(indicator);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
