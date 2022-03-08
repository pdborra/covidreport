package com.accenture.task.service.impl;

import com.accenture.task.constatnts.CovidConstants;
import com.accenture.task.exception.CovidUserDefinedException;
import com.accenture.task.model.Countries;
import com.accenture.task.service.CovidReportService;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired
    CacheManager cacheManager;

    @Override
    public double getCorrelationCoefficient(String input) {
        String vaccineUrl = CovidConstants.ALL_COUNTRIES_VACCINE_URL;
        String covidCaseUrl = CovidConstants.ALL_COUNTRIES_COVID_CASES_URL;

        if (input != null && !input.equals(CovidConstants.ALL)) {
            vaccineUrl = CovidConstants.CONTINENT_VACCINE_URL + input;
            covidCaseUrl = CovidConstants.CONTINENT_COVID_CASES_URL + input;
        }
        Map<String, Countries> vaccinationData = getCovidData(vaccineUrl);
        Map<String, Countries> covidDeathsData = getCovidData(covidCaseUrl);

        if (vaccinationData.size() == 0 || covidDeathsData.size() == 0) {
            throw new CovidUserDefinedException(CovidConstants.NO_DATA_FOUND);
        }
        return getCorrelationCoefficient(covidDeathsData, vaccinationData);
    }

    public double getCorrelationCoefficient(Map<String, Countries> covidDeathsMap, Map<String, Countries> vaccineMap) {

        List<Double> vaccinePercentageList = new ArrayList<>();
        List<Double> covidDeathsPercentageList = new ArrayList<>();

        covidDeathsMap.forEach((k, v) -> {

            if (vaccineMap.containsKey(k)) {
                Countries vaccineMapValues = vaccineMap.get(k);
                vaccinePercentageList.add(getPercentage(vaccineMapValues, CovidConstants.VACCINE));

                Countries covidDeathMapValues = covidDeathsMap.get(k);
                covidDeathsPercentageList.add(getPercentage(covidDeathMapValues, CovidConstants.COVID_DEATHS));

            }
        });

        return calculateCorrelationCoefficient(vaccinePercentageList, covidDeathsPercentageList);
    }

    @Cacheable(value = "countriesMap", key="#uri")
    public Map<String, Countries> getCovidData(String uri) {
        RestTemplate template = new RestTemplate();
        String result = template.getForObject(uri, String.class);
        JSONObject jsonObject = new JSONObject(result);
        Map<String, Object> map = jsonObject.toMap();

        Map<String, Countries> countriesMap = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, mapValue -> convertToCountries(mapValue.getValue()), (old, newVal) -> newVal, TreeMap::new));

        return countriesMap;
    }

    public Countries convertToCountries(Object map1) {
        Gson gson = new Gson();
        String vaccineJson = gson.toJson(map1);
        Countries countries = gson.fromJson(vaccineJson, Countries.class);
        return countries;
    }

    public double getPercentage(Countries countries, String type) {

        double percentage;

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

    public double calculateCorrelationCoefficient(List<Double> vaccinePercentages, List<Double> covidDeathsPercentages) {

        double vaccineSum = getSumOfElements(vaccinePercentages);
        double covidDeathSum = getSumOfElements(covidDeathsPercentages);
        double vaccineSquareSum = getSquareSumOfElements(vaccinePercentages);
        double covidDeathsSquare = getSquareSumOfElements(covidDeathsPercentages);
        double vaccineDeathsSum = getSumOfTwoListMultiplication(vaccinePercentages, covidDeathsPercentages);

        double n = vaccinePercentages.size();

        double correlationCoefficient = (n * vaccineDeathsSum - vaccineSum * covidDeathSum) /
                 (Math.sqrt((n * vaccineSquareSum -
                        vaccineSum * vaccineSum) * (n * covidDeathsSquare -
                        covidDeathSum * covidDeathSum)));

        return correlationCoefficient;

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

    public void evictAllCaches() {
        cacheManager.getCacheNames().stream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
    }

}