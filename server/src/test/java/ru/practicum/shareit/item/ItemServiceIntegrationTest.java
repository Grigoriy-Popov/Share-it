package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRequestService requestService;

    ItemDto itemDtoToCreate = ItemDto.builder().name("testItem").description("testDescription").available(true)
            .requestId(1L).build();
    ItemRequest itemRequestToCreate = ItemRequest.builder().description("testRequestDescription").build();
    User userToCreate = new User(null, "testUser", "test@email.com");

    @Test
    void createItemWithRequest() {
        User createdUser = userService.createUser(userToCreate);
        ItemRequest createdItemRequest = requestService.createRequest(itemRequestToCreate, 1L);

        ItemDto itemDto = itemService.createItem(itemDtoToCreate, 1L);
        Item item = itemRepository.findById(itemDto.getId()).orElse(new Item());

        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getName(), equalTo(itemDtoToCreate.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoToCreate.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoToCreate.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(createdUser.getId()));
        assertThat(item.getItemRequest().getId(), equalTo(createdItemRequest.getId()));
    }

    @Test
    void createItemWithoutRequest() {
        var itemDtoToCreateWithoutRequest = ItemDto.builder().name("testItem").description("testDescription")
                .available(true).build();
        User createdUser = userService.createUser(userToCreate);
        ItemDto itemDto = itemService.createItem(itemDtoToCreateWithoutRequest, 1L);

        Item item = itemRepository.findById(itemDto.getId()).orElse(new Item());

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

    @Test
    void getAllUserItems() {
        userService.createUser(userToCreate);
        requestService.createRequest(itemRequestToCreate, 1L);

        var itemDtoToCreate2 = ItemDto.builder().name("testItem2").description("testDescription2").available(true)
                .requestId(1L).build();

        ItemDto itemDto = itemService.createItem(itemDtoToCreate, 1L);
        ItemDto itemDto2 = itemService.createItem(itemDtoToCreate2, 1L);

        var userItems = itemService.getAllUserItems(1L, 0, 10);

        assertThat(userItems, hasSize(2));
        assertThat(userItems.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(userItems.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(userItems.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(userItems.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(userItems.get(0).getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(userItems.get(1).getId(), equalTo(itemDto2.getId()));
        assertThat(userItems.get(1).getName(), equalTo(itemDto2.getName()));
        assertThat(userItems.get(1).getDescription(), equalTo(itemDto2.getDescription()));
        assertThat(userItems.get(1).getAvailable(), equalTo(itemDto2.getAvailable()));
        assertThat(userItems.get(1).getRequestId(), equalTo(itemDto2.getRequestId()));
    }

    @Test
    void editItem() {
        userService.createUser(userToCreate);
        requestService.createRequest(itemRequestToCreate, 1L);

        itemService.createItem(itemDtoToCreate, 1L);

        var itemDtoToEditName = ItemDto.builder().name("updatedName").build();
        itemService.editItem(itemDtoToEditName, 1L, 1L);

        assertThat(itemService.getItemById(1L, 1L).getName(), equalTo(itemDtoToEditName.getName()));

        var itemDtoToEditDescription = ItemDto.builder().description("updatedDescription").build();
        itemService.editItem(itemDtoToEditDescription, 1L, 1L);

        assertThat(itemService.getItemById(1L, 1L).getDescription(),
                equalTo(itemDtoToEditDescription.getDescription()));

        var itemDtoToEditAvailable = ItemDto.builder().available(false).build();
        itemService.editItem(itemDtoToEditAvailable, 1L, 1L);

        assertThat(itemService.getItemById(1L, 1L).getAvailable(),
                equalTo(itemDtoToEditAvailable.getAvailable()));

        var itemDtoToEditFull = ItemDto.builder().name("updatedName2").description("updatedDescription2")
                .available(true).build();
        itemService.editItem(itemDtoToEditFull, 1L, 1L);

        assertThat(itemService.getItemById(1L, 1L).getName(), equalTo(itemDtoToEditFull.getName()));
        assertThat(itemService.getItemById(1L, 1L).getDescription(),
                equalTo(itemDtoToEditFull.getDescription()));
        assertThat(itemService.getItemById(1L, 1L).getAvailable(),
                equalTo(itemDtoToEditFull.getAvailable()));
    }

    @Test
    void searchAvailableItemsByKeyword() {
        var itemDtoUnavailable = ItemDto.builder().name("testName2").description("testDescription")
                .available(false).build();
        var itemDtoWithWrongText = ItemDto.builder().name("qqqqq").description("ddddddddd").available(true)
                .build();

        userService.createUser(userToCreate);
        requestService.createRequest(itemRequestToCreate, 1L);

        itemService.createItem(itemDtoToCreate, 1L);
        itemService.createItem(itemDtoUnavailable, 1L);
        itemService.createItem(itemDtoWithWrongText, 1L);

        var foundItemsList = itemService.searchAvailableItemsByKeyword("TeSt", 0, 10);

        assertThat(foundItemsList, hasSize(1));
        assertThat(foundItemsList.get(0).getId(), equalTo(1L));
    }

    @Test
    void addComment() {
        User user = userService.createUser(userToCreate);
        requestService.createRequest(itemRequestToCreate, 1L);
        itemService.createItem(itemDtoToCreate, 1L);
        Item item = itemRepository.findById(1L).orElse(new Item());
        var comment = Comment.builder().text("testText").item(item).author(user).build();
        var booking = Booking.builder().status(BookingStatus.APPROVED).booker(user).item(item)
                .start(LocalDateTime.of(1111, 1, 1, 1, 1, 1))
                .end(LocalDateTime.of(1111, 1, 1, 1, 1, 1))
                .build();
        bookingRepository.save(booking);
        Comment createdComment = itemService.addComment(comment, user.getId(), item.getId());

        assertThat(createdComment.getId(), equalTo(1L));
        assertThat(createdComment.getText(), equalTo(comment.getText()));
        assertThat(createdComment.getAuthor().getId(), equalTo(user.getId()));
        assertThat(createdComment.getItem().getId(), equalTo(1L));
    }
}
