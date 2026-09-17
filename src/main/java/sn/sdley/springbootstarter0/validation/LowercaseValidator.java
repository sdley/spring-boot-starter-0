package sn.sdley.springbootstarter0.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LowercaseValidator implements ConstraintValidator<Lowercase, Object> {
    @Override
    public void initialize(Lowercase constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof String stringValue)) {
            return false;
        }
        return stringValue.equals(stringValue.toLowerCase());
    }
}
