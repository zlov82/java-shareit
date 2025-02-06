package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private User booker;
    private Item item1;
    private Item item2;
    private LocalDateTime now;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();
        user = userRepository.save(User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build());

        booker = userRepository.save(User.builder()
                .email("test2@mail.com")
                .name("Booker")
                .build());

        item1 = itemRepository.save(Item.builder()
                .owner(user)
                .name("item1")
                .description("description1")
                .available(true)
                .build());

        item2 = itemRepository.save(Item.builder()
                .owner(user)
                .name("Item2")
                .description("Desc2")
                .available(true)
                .build());

        Booking pastBooking = Booking.builder()
                .item(item1)
                .user(booker)
                .start(now.minusDays(5))
                .end(now.minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        Booking currentBooking = Booking.builder()
                .item(item2)
                .user(booker)
                .start(now.minusHours(1))
                .end(now.plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();

        Booking futureBooking = Booking.builder()
                .item(item1)
                .user(booker)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        Booking rejectedBooking = Booking.builder()
                .item(item2)
                .user(booker)
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .status(BookingStatus.REJECTED)
                .build();

        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));
    }

    @Test
    void whenGetBookingsWithStatusALL_thenReturnCorrectBookings() {
        now = now.minusSeconds(3);

        bookingRepository.deleteAll();


        Booking booking = Booking.builder()
                .user(booker)
                .status(BookingStatus.APPROVED)
                .item(item1)
                .start(now.plusSeconds(1))
                .end(now.plusSeconds(2))
                .build();

        Booking booking2 = Booking.builder()
                .user(booker)
                .status(BookingStatus.APPROVED)
                .item(item2)
                .start(now.plusSeconds(10))
                .end(now.plusSeconds(12))
                .build();

        bookingRepository.saveAll(List.of(booking, booking2));

        List<Booking> bookings = bookingService.getBookingByUser(booker.getId(), BookingState.ALL.toString());

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().anyMatch(b -> b.getItem().getName().equals(item1.getName())));
        assertTrue(bookings.stream().anyMatch(b -> b.getItem().getName().equals(item2.getName())));
    }

    @Test
    void whenOwnerApprovesBooking_thenStatusChangesToApproved() {

        Booking booking = bookingRepository.save(Booking.builder()
                .user(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        Booking response = bookingService.updateBooking(user.getId(), booking.getId(), true);

        assertNotNull(response);
        assertEquals(BookingStatus.APPROVED, response.getStatus());
    }

    @Test
    void whenOwnerRejectsBooking_thenStatusChangesToRejected() {
        Booking booking = bookingRepository.save(Booking.builder()
                .user(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        Booking response = bookingService.updateBooking(user.getId(), booking.getId(), false);

        assertNotNull(response);
        assertEquals(BookingStatus.REJECTED, response.getStatus());
    }

    @Test
    void whenNonOwnerTriesToApproveBooking_thenThrowOwnerException() {

        Booking booking = bookingRepository.save(Booking.builder()
                .user(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        assertThrows(ForbiddenException.class, () ->
                bookingService.updateBooking(booker.getId(), booking.getId(), true)
        );
    }

    @Test
    void whenTryingToApproveNonExistingBooking_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.updateBooking(user.getId(), 999L, true) // 999L — ID несуществующего бронирования
        );
    }

    @Test
    void whenGetBookingsByNonExistingUser_thenThrowNotFoundException() {
        Long nonExistingUserId = 9999L;
        assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByUser(nonExistingUserId, BookingState.ALL.toString())
        );
    }

    @Test
    void whenGetBookingsWithStatusCURRENT_thenReturnOnlyCurrentBooking() {
        List<Booking> bookings = bookingService.getBookingByUser(booker.getId(), BookingState.CURRENT.toString());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking current = bookings.get(0);
        LocalDateTime nowTime = LocalDateTime.now();
        assertTrue(current.getStart().isBefore(nowTime) || current.getStart().isEqual(nowTime));
        assertTrue(current.getEnd().isAfter(nowTime) || current.getEnd().isEqual(nowTime));
        assertEquals(item2.getId(), current.getItem().getId());
    }

    @Test
    void whenGetBookingsWithStatusPAST_thenReturnOnlyPastBooking() {
        List<Booking> bookings = bookingService.getBookingByUser(booker.getId(), BookingState.PAST.toString());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking past = bookings.get(0);
        assertTrue(past.getEnd().isBefore(LocalDateTime.now()));
        assertEquals(item1.getId(), past.getItem().getId());
    }

    @Test
    void whenGetBookingsWithStatusFUTURE_thenReturnOnlyFutureBookings() {
        List<Booking> bookings = bookingService.getBookingByUser(booker.getId(), BookingState.FUTURE.toString());

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        LocalDateTime currentTime = LocalDateTime.now();
        for (Booking dto : bookings) {
            assertTrue(dto.getStart().isAfter(currentTime));
        }
    }

    @Test
    void whenGetBookingsWithStatusWAITING_thenReturnOnlyWaitingBookings() {
        List<Booking> bookings = bookingService.getBookingByUser(booker.getId(), BookingState.WAITING.toString());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking waiting = bookings.get(0);
        assertEquals(BookingStatus.WAITING, waiting.getStatus());
    }

    @Test
    void whenGetBookingsWithStatusREJECTED_thenReturnOnlyRejectedBookings() {
        List<Booking> bookings = bookingService.getBookingByUser(booker.getId(), BookingState.REJECTED.toString());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking rejected = bookings.get(0);
        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
    }

    @Test
    void whenCreateBookingSuccessfully_thenReturnBookingResponseDto() {
        CreateBookingRequest requestDto = CreateBookingRequest.builder()
                .itemId(item1.getId())
                .start(now.plusDays(2).plusSeconds(1))
                .end(now.plusDays(3))
                .build();

        Booking response = bookingService.saveBooking(booker.getId(), requestDto);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(requestDto.getStart(), response.getStart());
        assertEquals(requestDto.getEnd(), response.getEnd());
        assertEquals(BookingStatus.WAITING, response.getStatus());
        assertEquals(item1.getId(), response.getItem().getId());
        assertEquals(booker.getId(), response.getUser().getId());
    }


    @Test
    void whenCreateBookingWithNonExistingUser_thenThrowNotFoundException() {
        CreateBookingRequest requestDto = CreateBookingRequest.builder()
                .itemId(item1.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        Long nonExistingUserId = 9999L;

        assertThrows(NotFoundException.class, () ->
                bookingService.saveBooking(nonExistingUserId,requestDto)
        );
    }

    @Test
    void whenCreateBookingWithNonExistingItem_thenThrowNotFoundException() {

        CreateBookingRequest requestDto = CreateBookingRequest.builder()
                .itemId(9999L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () ->
                bookingService.saveBooking(booker.getId(), requestDto)
        );
    }

    @Test
    void whenCreateBookingForUnavailableItem_thenThrowUnavailableItemException() {
        item1.setAvailable(false);
        itemRepository.save(item1);

        CreateBookingRequest requestDto = CreateBookingRequest.builder()
                .itemId(item1.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        assertThrows(ForbiddenException.class, () ->
                bookingService.saveBooking(booker.getId(),requestDto)
        );
    }

    @Test
    void whenGetBookingByIdByUser_thenGet() {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .itemId(item1.getId())
                .start(now.minusDays(10))
                .end(now.minusDays(5))
                .build();

        Booking booking = bookingService.saveBooking(booker.getId(),request);
        Booking bookingResponse = bookingService.getBooking(booker.getId(),booking.getId());
        assertNotNull(bookingResponse);
        assertNotNull(bookingResponse.getId());
    }


    @Test
    void whenGetBookingByIllegalUser_thenThrow() {
        User testUser = userRepository.save(User.builder()
                .name("Test")
                .email("test@test.com")
                .build());

        assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(testUser.getId(),1L)
        );
    }

    /* тесты на получение по владельцу */
    @Test
    void whenGetBookingsOwnerWithStatusCURRENT_thenReturnOnlyCurrentBooking() {
        List<Booking> bookings = bookingService.getBookingByOwner(user.getId(), BookingState.CURRENT.toString());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking current = bookings.get(0);
        LocalDateTime nowTime = LocalDateTime.now();
        assertTrue(current.getStart().isBefore(nowTime) || current.getStart().isEqual(nowTime));
        assertTrue(current.getEnd().isAfter(nowTime) || current.getEnd().isEqual(nowTime));
        assertEquals(item2.getId(), current.getItem().getId());
    }

    @Test
    void whenGetBookingsOwnerWithStatusPAST_thenReturnOnlyPastBooking() {
        List<Booking> bookings = bookingService.getBookingByOwner(user.getId(), BookingState.PAST.toString());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking past = bookings.get(0);
        assertTrue(past.getEnd().isBefore(LocalDateTime.now()));
        assertEquals(item1.getId(), past.getItem().getId());
    }

    @Test
    void whenGetBookingsOwnerWithStatusFUTURE_thenReturnOnlyFutureBookings() {
        List<Booking> bookings = bookingService.getBookingByOwner(user.getId(), BookingState.FUTURE.toString());

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        LocalDateTime currentTime = LocalDateTime.now();
        for (Booking dto : bookings) {
            assertTrue(dto.getStart().isAfter(currentTime));
        }
    }

    @Test
    void whenGetBookingsOwnerWithStatusWAITING_thenReturnOnlyWaitingBookings() {
        List<Booking> bookings = bookingService.getBookingByOwner(user.getId(), BookingState.WAITING.toString());

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking waiting = bookings.get(0);
        assertEquals(BookingStatus.WAITING, waiting.getStatus());
    }

    @Test
    void whenGetBookingsOwnerWithStatusREJECTED_thenReturnOnlyRejectedBookings() {
        List<Booking> bookings = bookingService.getBookingByOwner(user.getId(), BookingState.REJECTED.toString());
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        Booking rejected = bookings.get(0);
        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
    }
}
