package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> getAllByOwnerIdOrderById(Long userId, Pageable page);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND upper(i.name) LIKE upper(concat('%', :text, '%')) " +
            "OR i.available = true AND upper(i.description) LIKE upper(concat('%', :text, '%'))")
    List<Item> searchAvailableItemsByKeyword(String text, Pageable page);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);

}
