package com.accenture.task.controller;

import com.accenture.task.service.CovidReportService;
import com.accenture.task.service.impl.CovidReportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CovidReportController.class)
public class CovidReportControllerTest {

    @MockBean
    CovidReportService covidReportService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getCorrelationCoefficientForAllCountriesTest() throws Exception {

        Mockito.when(covidReportService.getCorrelationCoefficient(Mockito.any())).thenReturn(1.1f);
        mockMvc.perform(get("/covid/allCountries"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCorrelationCoefficientForContinentTest() throws Exception {

        Mockito.when(covidReportService.getCorrelationCoefficient(Mockito.any())).thenReturn(1.1f);
        mockMvc.perform(get("/covid/continent").param("continent", "Europe"))
                .andExpect(status().isOk());
    }
}
