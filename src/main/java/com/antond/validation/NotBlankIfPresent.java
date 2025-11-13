package com.antond.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation that checks if a field is not blank when it is present (non-null).
 * This annotation is useful for optional fields where if a value is provided, it must not be empty
 * or blank. Unlike @NotBlank which always requires a non-blank value, this annotation allows null
 * values but validates non-null values to ensure they are not blank.
 *
 * @see NotBlankIfPresentValidator
 */
@Documented
@Constraint(validatedBy = NotBlankIfPresentValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankIfPresent {

  /**
   * The error message template when validation fails. Can be overridden when applying the
   * annotation.
   *
   * @return the error message template
   */
  String message() default "Field cannot be empty if provided";

  /**
   * Allows the specification of validation groups, to which this constraint belongs. This can be
   * used to perform conditional validation based on the group.
   *
   * @return the validation groups
   */
  Class<?>[] groups() default {};

  /**
   * Can be used by clients of the Bean Validation API to assign custom payload objects to a
   * constraint. This attribute is not used by the API itself.
   *
   * @return the custom payload classes
   */
  Class<? extends Payload>[] payload() default {};
}