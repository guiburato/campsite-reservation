package Backend.Tech.Challenge.upgrade.controller;

import Backend.Tech.Challenge.upgrade.repository.BookingDateRepository;
import Backend.Tech.Challenge.upgrade.repository.BookingRepository;
import Backend.Tech.Challenge.upgrade.repository.entity.BookingDate;
import Backend.Tech.Challenge.upgrade.repository.entity.BookingEntity;
import Backend.Tech.Challenge.upgrade.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BookingRepository bookingRepository;

  @MockBean
  private BookingDateRepository bookingDateRepository;

  @SpyBean
  private BookingService bookingService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  public static BookingEntity createBookingEntity(int qtdDaysArrival, int qtdDaysDeparture) {
    return BookingEntity.builder()
            .id(1)
            .email("gui@gmail.com")
            .name("Gui")
            .arrivalDate(LocalDate.now().plusDays(qtdDaysArrival))
            .departureDate(LocalDate.now().plusDays(qtdDaysDeparture))
            .build();
  }

  @Test
  void getBookingList_success() throws Exception {
    final var bookingEntityList = List.of(createBookingEntity(1,3));
    final var bookingList = bookingEntityList.stream().map(BookingEntity::toDto).collect(Collectors.toList());
    final var bookingListJson = objectMapper.writeValueAsString(bookingList);
    when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookingEntityList);
    mockMvc.perform(get("/booking"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(bookingListJson));
  }

  @Test
  void getBookingList_empty() throws Exception {
    final List<BookingEntity> bookingEntityList = List.of();
    final var bookingListJson = objectMapper.writeValueAsString(bookingEntityList);
    when(bookingRepository.findAll(any(Sort.class))).thenReturn(bookingEntityList);
    mockMvc.perform(get("/booking"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(bookingListJson));
  }

  @Test
  void getBooking_success() throws Exception {
    final var bookingEntity = createBookingEntity(1,3);
    final var bookingJson = objectMapper.writeValueAsString(bookingEntity.toDto());
    when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.of(bookingEntity));
    mockMvc.perform(get("/booking/" + bookingEntity.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(bookingJson));
  }

  @Test
  void getBooking_notFound() throws Exception {
    final var bookingEntity = createBookingEntity(1,3);
    when(bookingRepository.findById(bookingEntity.getId())).thenReturn(Optional.empty());
    mockMvc.perform(get("/booking/" + bookingEntity.getId()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("Couldn't find any booking with id " + bookingEntity.getId())));
  }

  @Test
  void addBooking_success() throws Exception {
    final var bookingEntity = createBookingEntity(1,3);
    final var bookingJson = objectMapper.writeValueAsString(bookingEntity);
    final var expectedBookingJson = objectMapper.writeValueAsString(bookingEntity);
    when(bookingRepository.save(any())).thenReturn(bookingEntity);
    mockMvc.perform(post("/booking").contentType(MediaType.APPLICATION_JSON).content(bookingJson))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedBookingJson));
  }

  @Test
  void addBooking_internalServerError() throws Exception {
    final var bookingEntity = createBookingEntity(1,3);
    final var bookingJson = objectMapper.writeValueAsString(bookingEntity);
    when(bookingRepository.save(any())).thenThrow(new RuntimeException("exception message"));
    mockMvc.perform(post("/booking").contentType(MediaType.APPLICATION_JSON).content(bookingJson))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("exception message")));
  }

  @Test
  void addBooking_notAvailable() throws Exception {
    final var bookingEntity = createBookingEntity(1,3);
    final var bookingJson = objectMapper.writeValueAsString(bookingEntity);
    final var alreadyBookedDate = bookingEntity.getArrivalDate();
    when(bookingDateRepository.lockfindAllByDateIsBetween(any(), any()))
            .thenReturn(new ArrayList<>(List.of(BookingDate.builder().date(alreadyBookedDate).build())));
    mockMvc.perform(post("/booking").contentType(MediaType.APPLICATION_JSON).content(bookingJson))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString(
                    MessageFormat.format("Dates [BookingDate(date={0})] are not available", alreadyBookedDate))));
  }

  @Test
  void updateBooking() throws Exception {
    final var oldBooking = createBookingEntity(1,3);
    final var newBooking = createBookingEntity(2,3);
    final var bookingJson = objectMapper.writeValueAsString(newBooking);
    when(bookingRepository.findById(any())).thenReturn(Optional.of(oldBooking));
    when(bookingRepository.save(newBooking)).then(returnsFirstArg());
    mockMvc.perform(put("/booking/1").contentType(MediaType.APPLICATION_JSON).content(bookingJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(bookingJson));
  }

  @Test
  void deleteBooking_success() throws Exception {
    mockMvc.perform(delete("/booking/1"))
            .andDo(print())
            .andExpect(status().isOk());
  }

  @Test
  void deleteBooking_failure() throws Exception {
    doThrow(EmptyResultDataAccessException.class).when(bookingRepository).deleteById(any());
    mockMvc.perform(delete("/booking/1"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("Couldn't find any booking with id 1")));
  }


  @Test
  void getAvailabilitiesBetween_no_param() throws Exception {
    final var argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);

    mockMvc.perform(get("/booking/availabilities"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(bookingService).getAvailabilities(argumentCaptor.capture(), argumentCaptor.capture());
    assertThat(argumentCaptor.getAllValues()).containsExactly(LocalDate.now(), LocalDate.now().plusMonths(1));
  }

  @Test
  void getAvailabilitiesBetween_start_param() throws Exception {
    final var argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
    final var start = "2021-01-28";
    final var startDate = LocalDate.parse(start);

    mockMvc.perform(get("/booking/availabilities")
                    .queryParam("start", start))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(bookingService).getAvailabilities(argumentCaptor.capture(), argumentCaptor.capture());
    assertThat(argumentCaptor.getAllValues()).containsExactly(startDate, startDate.plusMonths(1));
  }

  @Test
  void getAvailabilitiesBetween_end_param() throws Exception {
    final var argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
    final var endDate = LocalDate.now().plusDays(5);

    mockMvc.perform(get("/booking/availabilities")
                    .queryParam("end", endDate.toString()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    verify(bookingService).getAvailabilities(argumentCaptor.capture(), argumentCaptor.capture());
    assertThat(argumentCaptor.getAllValues()).containsExactly(LocalDate.now(), endDate);
  }

  @Test
  void getAvailabilitiesBetween_start_after_end() throws Exception {
    mockMvc.perform(get("/booking/availabilities")
                    .queryParam("start", "2022-10-23")
                    .queryParam("end", "2022-10-22"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("Start date 2022-10-23 is after end date 2022-10-22")));
  }


}