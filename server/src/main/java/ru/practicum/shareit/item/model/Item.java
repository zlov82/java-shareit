package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Transient
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @Transient
    private LocalDateTime lastBooking;

    @Transient
    private LocalDateTime nextBooking;

    @ManyToOne
    @JoinColumn(name = "request_id")
    @Builder.Default
    private ItemRequest itemRequest = null;
}