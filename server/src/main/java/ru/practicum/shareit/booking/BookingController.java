package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exceptions.ForbiddenException;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping()
    public List<BookingDto> getBookingByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingByUser(userId, state).stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingByOwner(userId, state).stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable Long bookingId) {
        return BookingMapper.toBookingDto(bookingService.getBooking(userId, bookingId));
    }


    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody CreateBookingRequest createBookingRequest) {

        if (createBookingRequest.getStart().isEqual(createBookingRequest.getEnd())) {
            throw new ForbiddenException("Время начала брони не может совпадать в временем конца брони");
        }
        return BookingMapper.toBookingDto(bookingService.saveBooking(userId, createBookingRequest));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return BookingMapper.toBookingDto(bookingService.updateBooking(userId, bookingId, approved));
    }
}
