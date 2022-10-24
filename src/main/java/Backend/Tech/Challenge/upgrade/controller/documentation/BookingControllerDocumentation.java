package Backend.Tech.Challenge.upgrade.controller.documentation;

import Backend.Tech.Challenge.upgrade.dto.BookingDto;
import Backend.Tech.Challenge.upgrade.dto.BookingForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")})
public interface BookingControllerDocumentation {

    @Operation(summary = "Book the campsite")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookingDto.class))})})
    ResponseEntity<BookingDto> include(@RequestBody @Valid BookingForm bookingForm);

    @Operation(summary = "Update the reservation by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookingDto.class))})})
    ResponseEntity<BookingDto> update(@PathVariable long id, @RequestBody @Valid BookingForm booking);

    @Operation(summary = "Cancel the reservation by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200")})
    void delete(@PathVariable long id);

    @Operation(summary = "Get the list of all reservations.")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookingDto.class))})
    ResponseEntity<List<BookingDto>> findAll();

    @Operation(summary = "Get the reservation by id")
    @ApiResponse(responseCode = "200", description = "ok",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BookingDto.class))})
    ResponseEntity<BookingDto> findById(@PathVariable long id);

    @Operation(summary = "Get all availability of the campsite for a given date range. There is one-month default date since the current date.")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LocalDate.class))})
    ResponseEntity<List<LocalDate>> getAvailabilities(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false)
            @Parameter(name = "Start date", description = "Start date, default: today")
            LocalDate start,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false)
            @Parameter(name = "End date", description = "End date, default: start date + 1 month")
            LocalDate end);
}
