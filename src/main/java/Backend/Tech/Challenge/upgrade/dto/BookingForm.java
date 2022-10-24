package Backend.Tech.Challenge.upgrade.dto;

import Backend.Tech.Challenge.upgrade.dto.validation.BookingConstraint;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Value
@Builder
@BookingConstraint
public class BookingForm {

  @NotBlank(message = "Name cannot be blank")
  String name;

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email cannot be blank")
  String email;

  @Future(message = "ArrivalDate should be in the future")
  @NotNull(message = "ArrivalDate is mandatory")
  LocalDate arrivalDate;

  @Future(message = "DepartureDate should be in the future")
  @NotNull(message = "DepartureDate is mandatory")
  LocalDate departureDate;

}
