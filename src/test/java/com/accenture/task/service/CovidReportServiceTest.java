package com.accenture.task.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CovidReportServiceTest {

    @InjectMocks
    CovidReportService covidReportService;

    @Test

    public void getCorrelationCoefficientForAllCountriesTest() {
        float value = covidReportService.getCorrelationCoefficientForAllCountries("All");
        Assertions.assertNotNull(value);

    }

    @Test
    public void getCorrelationCoefficientForContinent() {
        float value = covidReportService.getCorrelationCoefficientForContinent("Europe");
        Assertions.assertNotNull(value);

    }
}
