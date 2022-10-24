package Backend.Tech.Challenge.upgrade.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BookingValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingConstraint {
  String message() default "The booking does not respect the constraints";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
