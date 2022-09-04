package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> getAllByOwnerId(Long userId, Pageable page);

    @Query("select i from Item i " +
            "where i.available = true and upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or i.available = true and upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> searchAvailableItemsByKeyword(String text, Pageable page);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);
}
