package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService requestService;

    ItemDto itemDtoToCreate = ItemDto.builder()
            .id(null)
            .name("testItem")
            .description("testDescription")
            .available(true)
            .requestId(1L)
            .build();

    ItemRequest itemRequestToCreate = ItemRequest.builder()
            .id(null)
            .description("testRequestDescription")
            .build();

    User userToCreate = new User(null, "testUser", "test@email.com");

    Booking lastBooking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .build();

    Booking nextBooking = Booking.builder()
            .id(2L)
            .item(item)
            .booker(user)
            .build();

    @Test
    void createItemWithRequest() {
        User createdUser = userService.createUser(userToCreate);
        ItemRequest createdItemRequest = requestService.createRequest(itemRequestToCreate, 1L);

        ItemDto itemDto = itemService.createItem(itemDtoToCreate, 1L);

        Item item = itemRepository.findById(itemDto.getId()).orElse(null);

        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo(itemDtoToCreate.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoToCreate.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoToCreate.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(createdUser.getId()));
        assertThat(item.getItemRequest().getId(), equalTo(createdItemRequest.getId()));
    }

    @Test
    void createItemWithoutRequest() {
        ItemDto itemDtoToCreateWithoutRequest = ItemDto.builder()
                .id(null)
                .name("testItem")
                .description("testDescription")
                .available(true)
                .build();
        User createdUser = userService.createUser(userToCreate);
        ItemDto itemDto = itemService.createItem(itemDtoToCreateWithoutRequest, 1L);

        Item item = itemRepository.findById(itemDto.getId()).orElse(null);

        assert item != null;
        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo(itemDtoToCreate.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoToCreate.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoToCreate.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(createdUser.getId()));
        assertThat(item.getItemRequest(), nullValue());
    }

    @Test
    void getItemById() {
        userService.createUser(userToCreate);
        requestService.createRequest(itemRequestToCreate, 1L);

        ItemDto itemDto = itemService.createItem(itemDtoToCreate, 1L);

        ItemDto itemDto2 = itemService.getItemById(1L, 1L);

        assertThat(itemDto.getId(), equalTo(itemDto2.getId()));
        assertThat(itemDto.getName(), equalTo(itemDto2.getName()));
        assertThat(itemDto.getDescription(), equalTo(itemDto2.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(itemDto2.getAvailable()));
        assertThat(itemDto.getRequestId(), equalTo(itemDto2.getRequestId()));
    }
}
