package com.accenture.task.service.impl;

import com.accenture.task.constatnts.CovidConstants;
import com.accenture.task.exception.CustomException;
import com.accenture.task.model.Countries;
import com.accenture.task.service.CovidReportService;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Service
public class CovidReportServiceImpl implements CovidReportService {

    @Override
    public float getCorrelationCoefficient(String input) {
        String vaccineUrl = CovidConstants.ALL_COUNTRIES_VACCINE_URL;
        String covidCaseUrl = CovidConstants.ALL_COUNTRIES_COVID_CASES_URL;

        if(input != null && !input.equals(CovidConstants.ALL)){
            vaccineUrl = CovidConstants.CONTINENT_VACCINE_URL + input;
            covidCaseUrl = CovidConstants.CONTINENT_COVID_CASES_URL + input;
        }
        Map<String, Object> vaccinationData = getCovidData(vaccineUrl);
        Map<String, Object> covidDeathsData = getCovidData(covidCaseUrl);

        if (vaccinationData.size() == 0 || covidDeathsData.size() == 0) {
            throw new CustomException(CovidConstants.NO_DATA_FOUND);
        }
        return getCorrelationCoefficient(covidDeathsData, vaccinationData);
    }

    public float getCorrelationCoefficient(Map<String, Object> covidDeathsMap, Map<String, Object> vaccineMap) {

        List<Double> vaccinePercentageList = new ArrayList<>();
        List<Double> covidDeathsPercentageList = new ArrayList<>();

        covidDeathsMap.forEach((k, v) -> {

            if (vaccineMap.containsKey(k)) {
                Object vaccineMapValues = vaccineMap.get(k);
                vaccinePercentageList.add(getPercentage(vaccineMapValues, CovidConstants.VACCINE));

                Object covidDeathMapValues = covidDeathsMap.get(k);
                covidDeathsPercentageList.add(getPercentage(covidDeathMapValues, CovidConstants.COVID_DEATHS));

            }
        });

        return calculateCorrelationCoefficient(vaccinePercentageList, covidDeathsPercentageList);
    }

    public Map<String, Object> getCovidData(String uri) {
        RestTemplate template = new RestTemplate();
        String result = template.getForObject(uri, String.class);
        JSONObject object = new JSONObject(result);
        Map<String, Object> map = object.toMap();

        Map<String, Object> treeMap = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (old, newVal) -> newVal, TreeMap::new));

        return treeMap;
    }


    public double getPercentage(Object value, String type) {

        double percentage;

        Gson gson = new Gson();
        String vaccineJson = gson.toJson(value);
        Countries countries = gson.fromJson(vaccineJson, Countries.class);

        if (type.equals(CovidConstants.VACCINE)) {
            percentage = calculatePercentage(countries.getAll().getPopulation(), countries.getAll().getPeople_vaccinated());
        } else {
            percentage = calculatePercentage(countries.getAll().getPopulation(), countries.getAll().getDeaths());
        }

        return percentage;
    }

    public double calculatePercentage(double total, double input) {

        if (input == 0 || total == 0) {
            return 0;
        }

        return (input / total) * 100;
    }

    public float calculateCorrelationCoefficient(List<Double> vaccinePercentages, List<Double> covidDeathsPercentages) {

        double vaccineSum = getSumOfElements(vaccinePercentages);
        double covidDeathSum = getSumOfElements(covidDeathsPercentages);
        double vaccineSquareSum = getSquareSumOfElements(vaccinePercentages);
        double covidDeathsSquare = getSquareSumOfElements(covidDeathsPercentages);
        double vaccineDeathsSum = getSumOfTwoListMultiplication(vaccinePercentages, covidDeathsPercentages);

        double n = vaccinePercentages.size();

        float corr = (float) (n * vaccineDeathsSum - vaccineSum * covidDeathSum) /
                (float) (Math.sqrt((n * vaccineSquareSum -
                        vaccineSum * vaccineSum) * (n * covidDeathsSquare -
                        covidDeathSum * covidDeathSum)));

        return corr;

    }

    public double getSumOfElements(List<Double> elements) {

        return elements.stream().flatMapToDouble(value -> DoubleStream.of(value)).sum();
    }

    public double getSquareSumOfElements(List<Double> elements) {

        return elements.stream().map(value -> value * value).flatMapToDouble(result -> DoubleStream.of(result)).sum();
    }

    public double getSumOfTwoListMultiplication(List<Double> vaccineElements, List<Double> covidDeathElements) {

        return IntStream.range(0, vaccineElements.size()).mapToObj(i -> vaccineElements.get(i) * covidDeathElements.get(i))
                .flatMapToDouble(value -> DoubleStream.of(value)).sum();
    }

}