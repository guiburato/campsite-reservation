package Backend.Tech.Challenge.upgrade.repository;

import Backend.Tech.Challenge.upgrade.repository.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

}
