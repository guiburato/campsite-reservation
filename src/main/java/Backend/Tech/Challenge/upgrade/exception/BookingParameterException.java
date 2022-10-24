package Backend.Tech.Challenge.upgrade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BookingParameterException extends RuntimeException {

  public BookingParameterException(String message) {
    super(message);
  }
}
