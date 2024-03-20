package com.shubham.User.custumAnnotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AgeLimitImpl implements ConstraintValidator<AgeLimit, String> {

    private int minAge;
    @Override
    public void initialize(AgeLimit constraintAnnotation) {
        this.minAge = constraintAnnotation.minAge();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String dateStr, ConstraintValidatorContext constraintValidatorContext) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate birthDate = LocalDate.parse(dateStr, formatter);
            LocalDate currentDate = LocalDate.now();

            Period age = Period.between(birthDate, currentDate);

            return age.getYears() >= minAge;
        } catch (Exception e) {
            return false;
        }
    }
}
