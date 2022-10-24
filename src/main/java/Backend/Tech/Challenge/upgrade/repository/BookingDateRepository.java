package Backend.Tech.Challenge.upgrade.repository;

import Backend.Tech.Challenge.upgrade.repository.entity.BookingDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingDateRepository extends JpaRepository<BookingDate, LocalDate> {
  String QUERY = "select a from BookingDate a where a.date >= ?1 and a.date < ?2";

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(QUERY)
  List<BookingDate> lockfindAllByDateIsBetween(LocalDate startInclusive, LocalDate endExclusive);
  @Query(QUERY)
  List<BookingDate> findAllByDateIsBetween(LocalDate startInclusive, LocalDate endExclusive);
}
