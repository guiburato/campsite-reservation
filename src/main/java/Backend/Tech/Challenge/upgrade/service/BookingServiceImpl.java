package Backend.Tech.Challenge.upgrade.service;

import Backend.Tech.Challenge.upgrade.dto.BookingDto;
import Backend.Tech.Challenge.upgrade.dto.BookingForm;
import Backend.Tech.Challenge.upgrade.exception.BookingNotFoundException;
import Backend.Tech.Challenge.upgrade.exception.ConflictException;
import Backend.Tech.Challenge.upgrade.repository.BookingDateRepository;
import Backend.Tech.Challenge.upgrade.repository.BookingRepository;
import Backend.Tech.Challenge.upgrade.repository.entity.BookingDate;
import Backend.Tech.Challenge.upgrade.repository.entity.BookingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

  private final BookingRepository bookingRepository;

  private final BookingDateRepository bookingDateRepository;

  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public BookingDto include(BookingForm bookingForm) {
    log.info("BookingServiceImpl.newBooking -> bookingForm {}", bookingForm);
    final var bookingDates = bookingDateRepository.lockfindAllByDateIsBetween(
            bookingForm.getArrivalDate(), bookingForm.getDepartureDate());

    if (bookingDates.isEmpty()) {
      return save(BookingEntity.parseFormToEntity(bookingForm));
    } else {
      throw new ConflictException("Dates " + bookingDates + " are not available");
    }
  }

  private BookingDto save(BookingEntity bookingEntity) {
    log.info("BookingServiceImpl.save -> bookingEntity {}", bookingEntity);

    bookingDateRepository.saveAll(bookingEntity.bookingDates());
    return bookingRepository.save(bookingEntity).toDto();
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public BookingDto update(long bookingId, BookingForm bookingForm) {
    log.info("BookingServiceImpl.update() -> bookingId {}, bookingForm {}", bookingId, bookingForm);
    final var oldBookingEntity = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException(bookingId));

    bookingDateRepository.deleteAll(oldBookingEntity.bookingDates());

    var newBookingEntity = BookingEntity.parseFormToEntity(bookingForm);
    newBookingEntity.setId(oldBookingEntity.getId());

    return save(newBookingEntity);
  }

  @Transactional(readOnly = true)
  public BookingDto findById(long id) {
    log.info("BookingServiceImpl.findById() -> {}", id);
    return bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id)).toDto();
  }

  @Transactional
  public void deleteById(long id) {
    log.info("BookingServiceImpl.deleteById() -> {}", id);
    bookingRepository.findById(id).ifPresent(booking -> bookingDateRepository.deleteAll(booking.bookingDates()));
    bookingRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<BookingDto> findAll() {
    log.info("BookingServiceImpl.findAll()");
    return bookingRepository.findAll(Sort.by("arrivalDate")).stream().map(BookingEntity::toDto).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<LocalDate> getAvailabilities(LocalDate startDate, LocalDate endDate) {
    log.info("BookingServiceImpl.getAvailabilities() -> {}", startDate, endDate);
    var availableDates = startDate.datesUntil(endDate).collect(Collectors.toList());
    final var reservedDates = bookingDateRepository.findAllByDateIsBetween(startDate, endDate);
    availableDates.removeAll(reservedDates.stream().map(BookingDate::getDate).collect(Collectors.toList()));

    return availableDates;
  }
}
