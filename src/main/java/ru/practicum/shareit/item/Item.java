package ru.practicum.shareit.item;


import lombok.*;
import ru.practicum.shareit.booking.dto.ForItemBookingDto;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table (name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @Transient
    private ForItemBookingDto lastBooking;
    @Transient
    private ForItemBookingDto nextBooking;
}
