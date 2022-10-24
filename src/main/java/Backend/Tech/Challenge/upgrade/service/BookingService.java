package Backend.Tech.Challenge.upgrade.service;

import Backend.Tech.Challenge.upgrade.dto.BookingDto;
import Backend.Tech.Challenge.upgrade.dto.BookingForm;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDto include(BookingForm bookingForm);
    BookingDto update(long bookingId, BookingForm bookingForm);
    BookingDto findById(long id);
    void deleteById(long id);
    List<BookingDto> findAll();
    List<LocalDate> getAvailabilities(LocalDate startDate, LocalDate endDate);
}
