package com.accenture.task.controller;

import com.accenture.task.constatnts.CovidConstants;
import com.accenture.task.model.Countries;
import com.accenture.task.service.CovidReportService;
import com.accenture.task.service.impl.CovidReportServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/covid")
@Api(value = "Covid19 API - to serve the endpoints to calculate Correlation Coefficient")
public class CovidReportController {

    @Autowired
    CovidReportService covidReportService;

    @GetMapping(value = "/allCountries")
    @ApiOperation(value = "Endpoint to get Correlation Coefficient for allCountries ",
            produces = "application/json", response = Countries.class, httpMethod = "GET")
    public float getCorrelationCoefficientForAllCountries() {

        return covidReportService.getCorrelationCoefficient(CovidConstants.ALL);
    }

    @GetMapping(value = "/continent")
    @ApiOperation(value = "Endpoint to get Correlation Coefficient by continent ",
            produces = "application/json", response = Countries.class, httpMethod = "GET")
    public float getCorrelationCoefficientFoContinent(@RequestParam String continent) {

        return covidReportService.getCorrelationCoefficient(continent);
    }
}
