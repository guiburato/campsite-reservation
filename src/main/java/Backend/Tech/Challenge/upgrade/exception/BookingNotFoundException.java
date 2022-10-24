package Backend.Tech.Challenge.upgrade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BookingNotFoundException extends RuntimeException {
  public BookingNotFoundException(long id) {
    super("Couldn't find any booking with id " + id);
  }
}
