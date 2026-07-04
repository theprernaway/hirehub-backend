package com.hirehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String location;

    @PositiveOrZero(message = "Salary cannot be negative")
    private Double salary;
}
