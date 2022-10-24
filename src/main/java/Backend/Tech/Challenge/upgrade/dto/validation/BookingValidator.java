package Backend.Tech.Challenge.upgrade.dto.validation;

import Backend.Tech.Challenge.upgrade.configuration.CampsiteReservationConfiguration;
import Backend.Tech.Challenge.upgrade.dto.BookingForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@RequiredArgsConstructor
public class BookingValidator implements ConstraintValidator<BookingConstraint, BookingForm> {
  private final CampsiteReservationConfiguration configuration;

  @Override
  public void initialize(BookingConstraint constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(BookingForm booking, ConstraintValidatorContext context) {
    if (booking.getArrivalDate() == null || booking.getDepartureDate() == null) return true;

    context.disableDefaultConstraintViolation();
    var valid = true;

    if (booking.getDepartureDate().isBefore(booking.getArrivalDate())) {
      valid = invalidErrorMessage(context, "Arrival date should be before departure date");
    }

    final var stayInDays = DAYS.between(booking.getArrivalDate(), booking.getDepartureDate());
    if (stayInDays < 1) {
      valid = invalidErrorMessage(context, "The campsite can be reserved for minimum 1 day");
    }

    if (stayInDays > configuration.getMaxDaysBookingDuration()) {
      valid = invalidErrorMessage(context, "The campsite can be reserved for maximum " + configuration.getMaxDaysBookingDuration() + " days");
    }

    final var daysAheadOfArrival = DAYS.between(LocalDate.now(), booking.getDepartureDate());
    if (daysAheadOfArrival < configuration.getMinDaysBooking()) {
      valid = invalidErrorMessage(context, "The campsite can be reserved minimum " + configuration.getMinDaysBooking() + " day(s) ahead of arrival");
    }

    if (daysAheadOfArrival >  configuration.getMaxDaysBooking()) {
      valid = invalidErrorMessage(context, " The campsite can be reserved up to " + configuration.getMaxDaysBooking() + " day(s) in advance");
    }
    return valid;
  }

  private boolean invalidErrorMessage(ConstraintValidatorContext context, String message) {
    context
        .buildConstraintViolationWithTemplate(message)
        .addConstraintViolation();
    return false;
  }

}
