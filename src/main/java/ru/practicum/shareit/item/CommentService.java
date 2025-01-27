package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final UserService userService;
    private final CommentRepository repository;
    private final BookingRepository bookingRepository;

    public Comment addComment(long userId, Item item, String textComment) {
        LocalDateTime now = LocalDateTime.now();
        User user = userService.getById(userId);
        log.info("Поиск подвержденных бронирований вещи {} для клиента {} по времени {}", item.getId(), userId, now);
        if (!bookingRepository.existsByItemAndUserAndStatusAndEndBefore(item,user, BookingStatus.APPROVED, now)) {
            throw new ForbiddenException("Невозможно добавить комментарий");
        }

        Comment comment = Comment.builder()
                .text(textComment)
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        return repository.save(comment);
    }

    public List<Comment> getItemComments(Item item) {
        return repository.findAnyByItem(item);
    }
}
