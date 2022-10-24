package Backend.Tech.Challenge.upgrade.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Builder
@Value
public class BookingDto {

  long id;

  String name;

  String email;

  LocalDate arrivalDate;

  LocalDate departureDate;

}
