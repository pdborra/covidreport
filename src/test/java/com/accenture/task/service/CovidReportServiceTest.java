package com.accenture.task.service;

import com.accenture.task.service.impl.CovidReportServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CovidReportServiceTest {

    @InjectMocks
    CovidReportService covidReportService = new CovidReportServiceImpl();

    @Test
    public void getCorrelationCoefficientForAllCountriesTest() {
        double value = covidReportService.getCorrelationCoefficient("All");
        Assertions.assertNotNull(value);

    }

    @Test
    public void getCorrelationCoefficientForContinent() {
        double value = covidReportService.getCorrelationCoefficient("Europe");
        Assertions.assertNotNull(value);

    }
}
