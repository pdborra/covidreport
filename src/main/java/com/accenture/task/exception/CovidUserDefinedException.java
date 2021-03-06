package com.accenture.task.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CovidUserDefinedException extends RuntimeException {

    private String errorMessage;

}
