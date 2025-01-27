package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    public Booking saveBooking(long userId, CreateBookingRequest createBookingRequest) {
        User user = userService.getById(userId);
        Item item = itemService.getItemById(createBookingRequest.getItemId());
        if (!item.isAvailable()) {
            throw new ForbiddenException("Вещь недоступна для бронирования");
        }

        //todo: Проверить, что даты бронивания из запроса на бронь не совпадают с уже существующей бронью
        //

        Booking booking = BookingMapper.toBooking(createBookingRequest, user, item);
        log.info("Бронь для сохранения {}", booking);
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(long userId, Long bookingId, Boolean approved) {
        Booking booking = this.getBookingById(bookingId);

        if (userId != booking.getItem().getOwner().getId()) {
            throw new ForbiddenException("Нельзя менять статус брони данным пользователем");
        }

        if (approved == null || !approved) {
            booking.setStatus(BookingStatus.REJECTED);
        } else {
            //todo: Проверить, что даты бронивания из запроса на бронь не совпадают с уже существующей бронью
            booking.setStatus(BookingStatus.APPROVED);
        }

        return bookingRepository.save(booking);
    }

    public Booking getBooking(long userId, Long bookingId) {
        User user = userService.getById(userId);
        Booking booking = this.getBookingById(bookingId);
        long bookerId = booking.getUser().getId();
        long owner = booking.getItem().getOwner().getId();

        if (user.getId() != bookerId && user.getId() != owner) {
            throw new ForbiddenException("Нет доступа для просмотра бронивания текущему пользователю");
        }

        return booking;
    }

    public List<Booking> getBookingByUser(long userId, String state) {
        User user = userService.getById(userId);
        BookingState findState = BookingState.valueOf(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (findState) {
            case ALL -> bookingRepository.findAllByUserOrderByStartDesc(user);
            case PAST -> bookingRepository.findAllByUserAndEndBeforeOrderByStartDesc(user, now);
            case FUTURE -> bookingRepository.findAllByUserAndStartAfterOrderByStartDesc(user, now);
            case CURRENT -> bookingRepository.findAllByUserAndStartBeforeAndEndAfterOrderByStartDesc(user, now, now);
            case WAITING -> bookingRepository.findAllByUserAndStatusOrderByStartDesc(user, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByUserAndStatusOrderByStartDesc(user, BookingStatus.REJECTED);
        };

        return bookings;
    }

    public List<Booking> getBookingByOwner(long userId, String state) {
        User owner = userService.getById(userId);
        BookingState findState = BookingState.valueOf(state);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (findState) {
            case ALL -> bookingRepository.findAllByOwnerAll(owner);
            case PAST -> bookingRepository.findAllByOwnerPast(owner, now);
            case FUTURE -> bookingRepository.findAllByOwnerFuture(owner, now);
            case CURRENT -> bookingRepository.findAllByOwnerCurrent(owner, now);
            case WAITING -> bookingRepository.findAllByOwnerByStatus(owner, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByOwnerByStatus(owner, BookingStatus.REJECTED);
        };

        return bookings;
    }

    public boolean isItemBookingByUser(Item item, User user, BookingStatus status, LocalDateTime now) {
        return bookingRepository.existsByItemAndUserAndStatusAndEndBefore(item, user, status, now);
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронь не найдена"));
    }
}
