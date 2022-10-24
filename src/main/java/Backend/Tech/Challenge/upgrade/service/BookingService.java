package Backend.Tech.Challenge.upgrade.service;

import Backend.Tech.Challenge.upgrade.dto.BookingDto;
import Backend.Tech.Challenge.upgrade.dto.BookingForm;
import Backend.Tech.Challenge.upgrade.repository.entity.BookingEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDto include(BookingForm bookingForm);
    BookingDto update(long bookingId, BookingForm bookingForm);
    BookingDto findById(long id);
    void deleteById(long id);
    List<BookingDto> findAll();
    List<LocalDate> getAvailabilities(LocalDate startDate, LocalDate endDate);
}
