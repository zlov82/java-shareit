package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    String QUERY_OWNER_ALL = """
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            ORDER BY start DESC
            """;

    String QUERY_OWNER_CURRENT = """
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            AND b.start < :now AND b.end > :now
            ORDER BY start DESC
            """;

    String QUERY_OWNER_STATE = """
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            AND b.status = :status
            ORDER BY start DESC
            """;

    String QUERY_OWNER_PAST = """
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            AND b.end < :now
            ORDER BY start DESC
            """;

    String QUERY_OWNER_FUTURE = """
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            AND b.start > :now
            ORDER BY start ASC
            """;

    String QUERY_LAST_BOOKING = """
            SELECT MAX(b.start)
            FROM Booking as b
            WHERE b.item = :item
            AND b.status = 'APPROVED'
            AND b.end < :now
            """;

    String QUERY_NEXT_BOOKING = """
            SELECT MIN(b.end)
            FROM Booking as b
            WHERE b.item = :item
            AND b.status = 'APPROVED'
            AND b.start > :now
            """;

    List<Booking> findAllByUserOrderByStartDesc(User user);

    @Query(QUERY_OWNER_ALL)
    List<Booking> findAllByOwnerAll(@Param("owner") User owner);

    List<Booking> findAllByUserAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    @Query(QUERY_OWNER_CURRENT)
    List<Booking> findAllByOwnerCurrent(@Param("owner") User owner, @Param("now") LocalDateTime now);

    List<Booking> findAllByUserAndStatusOrderByStartDesc(User user, BookingStatus status);

    @Query(QUERY_OWNER_STATE)
    List<Booking> findAllByOwnerByStatus(@Param("owner") User owner, @Param("status") BookingStatus status);

    List<Booking> findAllByUserAndStartAfterOrderByStartDesc(User user, LocalDateTime start);

    @Query(QUERY_OWNER_FUTURE)
    List<Booking> findAllByOwnerFuture(@Param("owner") User owner, @Param("now") LocalDateTime now);

    List<Booking> findAllByUserAndEndBeforeOrderByStartDesc(User user, LocalDateTime end);

    @Query(QUERY_OWNER_PAST)
    List<Booking> findAllByOwnerPast(@Param("owner") User owner, @Param("now") LocalDateTime now);

    boolean existsByItemAndUserAndStatusAndEndBefore(Item item, User booker, BookingStatus status, LocalDateTime now);

    @Query(QUERY_LAST_BOOKING)
    LocalDateTime findLastBookingDate(Item item, LocalDateTime now);

    @Query(QUERY_NEXT_BOOKING)
    LocalDateTime findNextBookingDate(Item item, LocalDateTime now);
}
