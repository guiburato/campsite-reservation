package Backend.Tech.Challenge.upgrade.repository.entity;

import Backend.Tech.Challenge.upgrade.dto.BookingDto;
import Backend.Tech.Challenge.upgrade.dto.BookingForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Booking")
public class BookingEntity {

  @Id
  @GeneratedValue
  private long id;

  @Email
  private String email;

  private String name;

  private LocalDate arrivalDate;

  private LocalDate departureDate;

  public List<BookingDate> bookingDates() {
    return arrivalDate.datesUntil(departureDate).collect(Collectors.toList())
            .stream().map(date -> BookingDate.builder().date(date).build())
            .collect(Collectors.toList());
  }

  public static BookingEntity parseFormToEntity(BookingForm booking) {
    return BookingEntity.builder()
        .email(booking.getEmail())
        .name(booking.getName())
        .arrivalDate(booking.getArrivalDate())
        .departureDate(booking.getDepartureDate())
        .build();
  }

  public BookingDto toDto() {
    return BookingDto.builder()
            .id(id)
            .email(email)
            .name(name)
            .arrivalDate(arrivalDate)
            .departureDate(departureDate)
            .build();
  }
}
