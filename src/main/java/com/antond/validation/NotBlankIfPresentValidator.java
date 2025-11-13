package com.antond.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link NotBlankIfPresent} annotation. This validator checks that
 * if a string value is present (non-null), it must not be empty. Null values are considered valid
 * since the validation only applies when a value is provided.
 */
public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    return !value.trim().isEmpty();
  }
}
