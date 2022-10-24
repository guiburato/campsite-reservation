package Backend.Tech.Challenge.upgrade.controller;

import Backend.Tech.Challenge.upgrade.controller.documentation.BookingControllerDocumentation;
import Backend.Tech.Challenge.upgrade.dto.BookingDto;
import Backend.Tech.Challenge.upgrade.dto.BookingForm;
import Backend.Tech.Challenge.upgrade.exception.BadRequestException;
import Backend.Tech.Challenge.upgrade.exception.BookingNotFoundException;
import Backend.Tech.Challenge.upgrade.exception.ConflictException;
import Backend.Tech.Challenge.upgrade.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/booking" ,produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class BookingController implements BookingControllerDocumentation {

  private final BookingService bookingService;

  @PostMapping
  @Override
  public ResponseEntity<BookingDto> include(@Valid @RequestBody BookingForm booking) {
    try {
      log.info("BookingController.newBooking -> {}", booking);
      return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(bookingService.include(booking));
    } catch (DataAccessException e) {
      throw new ConflictException("Dates are not available");
    }
  }

  @PutMapping(path = "/{id}")
  @Override
  public ResponseEntity<BookingDto> update(@PathVariable long id, @Valid @RequestBody BookingForm booking) {
    log.info("BookingController.updateBooking -> id {}, booking {}", id, booking);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(bookingService.update(id, booking));
  }

  @DeleteMapping(path = "/{id}")
  @Override
  public void delete(@PathVariable long id) {
    log.info("BookingController.deleteBooking -> id {}", id);
    try {
      bookingService.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new BookingNotFoundException(id);
    }
  }

  @GetMapping
  @Override
  public ResponseEntity<List<BookingDto>> findAll() {
    log.info("BookingController.getAllBooking -> {}");
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(bookingService.findAll());
  }

  @GetMapping(path ="/{id}")
  @Override
  public ResponseEntity<BookingDto> findById(@PathVariable long id) {
    log.info("BookingController.getBooking -> id {}", id);
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(bookingService.findById(id));
  }

  @GetMapping(path = "/availabilities")
  @Override
  public ResponseEntity<List<LocalDate>> getAvailabilities(LocalDate start, LocalDate end) {
    log.info("BookingController.getAvailabilities -> start {} , end {}", start, end);
    if (start == null) start = LocalDate.now();
    if (end == null) end = start.plusMonths(1);

    if (start.isAfter(end)) {
      throw new BadRequestException(MessageFormat.format("Start date {0} is after end date {1}", start, end));
    }
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(bookingService.getAvailabilities(start, end));

  }

}
