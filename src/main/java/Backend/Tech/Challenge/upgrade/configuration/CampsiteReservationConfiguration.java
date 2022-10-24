package Backend.Tech.Challenge.upgrade.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class CampsiteReservationConfiguration {

    @Value("${variables.maxDaysBookingDuration}")
    private int maxDaysBookingDuration;
    @Value("${variables.minDaysBooking}")
    private int minDaysBooking;
    @Value("${variables.maxDaysBooking}")
    private int maxDaysBooking;
}
