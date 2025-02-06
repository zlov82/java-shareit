package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private Long userId = 1L;
    private Long ownerId = 2L;
    private Long bookingId = 1L;
    private CreateBookingRequest bookingRequestDto;
    private Booking bookingResponse;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        bookingRequestDto = CreateBookingRequest.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2024, 1, 10, 10, 0))
                .end(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        bookingResponse = Booking.builder()
                .id(bookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(Item.builder()
                        .id(10L)
                        .name("Test Item")
                        .description("Test Description")
                        .available(true)
                        .owner(User.builder()
                                .id(ownerId)
                                .name("ItemOwner")
                                .email("owner@email.com")
                                .build())
                        .build())
                .user(User.builder()
                        .id(userId)
                        .name("UserName")
                        .email("user@email.com")
                        .build())
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create() throws Exception {
        when(bookingService.saveBooking(userId, bookingRequestDto))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(10L))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).saveBooking(userId, bookingRequestDto);
    }

    @Test
    void setApprove() throws Exception {
        Booking approvedResponse = Booking.builder()
                .id(bookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(bookingResponse.getItem())
                .user(bookingResponse.getUser())
                .status(BookingStatus.APPROVED)
                .build();

        Boolean approved = true;
        when(bookingService.updateBooking(userId, bookingId, approved))
                .thenReturn(approvedResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", approved.toString())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).updateBooking(userId, bookingId, approved);
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(bookingId, userId))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(10L))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void getBookings() throws Exception {
        Booking bookingResponse2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 2, 10, 10, 0))
                .end(LocalDateTime.of(2023, 2, 12, 10, 0))
                .item(Item.builder().id(5L).name("Item2").owner(User.builder()
                        .id(ownerId)
                        .name("Owner")
                        .email("owner@email.com")
                        .build()).build())
                .user(bookingResponse.getUser())
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.getBookingByUser(userId, BookingState.ALL.toString()))
                .thenReturn(List.of(bookingResponse, bookingResponse2));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(bookingService, times(1)).getBookingByUser(userId, BookingState.ALL.toString());
    }

    @Test
    void getBookingsByOwnerItems() throws Exception {
        when(bookingService.getBookingByOwner(userId, BookingState.ALL.toString()))
                .thenReturn(List.of(bookingResponse));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].item.id").value(10L));

        verify(bookingService, times(1)).getBookingByOwner(userId, BookingState.ALL.toString());
    }

}
