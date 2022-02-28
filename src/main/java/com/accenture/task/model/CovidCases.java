package com.accenture.task.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class CovidCases implements Serializable {

    private int confirmed;
    private double recovered;
    private double deaths;
    private double people_vaccinated;
    private double people_partially_vaccinated;
    private double population;
    private String country;
    private String continent;

}
