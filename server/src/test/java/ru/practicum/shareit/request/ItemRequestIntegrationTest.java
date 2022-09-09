package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final ItemRepository itemRepository;
    private final UserService userService;

    User requester = new User(null, "testUser", "test@email.com");
    User itemOwner = new User(null, "testUser", "test2@email.com");
    ItemRequest itemRequestToCreate = ItemRequest.builder().description("testDescr").build();

    @Test
    void createRequest() {
        User createdUser = userService.createUser(requester);

        ItemRequest createdItemRequest = itemRequestService.createRequest(itemRequestToCreate, 1L);

        assertThat(createdItemRequest.getId(), equalTo(1L));
        assertThat(createdItemRequest.getDescription(), equalTo(itemRequestToCreate.getDescription()));
        assertThat(createdItemRequest.getRequester(), equalTo(createdUser));
    }

    @Test
    void getAllUserRequests() {
        userService.createUser(requester);
        User createdOwner = userService.createUser(itemOwner);
        ItemRequest createdItemRequest = itemRequestService.createRequest(itemRequestToCreate, 1L);
        Item item = Item.builder().name("testName").description("testDescription").itemRequest(createdItemRequest)
                .available(true).owner(createdOwner).build();
        itemRepository.save(item);

        var userRequestsList = itemRequestService.getAllUserRequests(1L);

        assertThat(userRequestsList, hasSize(1));
        assertThat(userRequestsList.get(0).getId(), equalTo(1L));
        assertThat(userRequestsList.get(0).getItems(), hasSize(1));
    }

    @Test
    void getAllRequests_shouldReturnListWhenNotRequesterFindRequests() {
        userService.createUser(requester);
        User createdOwner = userService.createUser(itemOwner);
        ItemRequest createdItemRequest = itemRequestService.createRequest(itemRequestToCreate, 1L);
        Item item = Item.builder().name("testName").description("testDescription").itemRequest(createdItemRequest)
                .available(true).owner(createdOwner).build();
        itemRepository.save(item);

        var requestsList = itemRequestService.getAllRequests(2L, 0, 10);

        assertThat(requestsList, hasSize(1));
        assertThat(requestsList.get(0).getId(), equalTo(1L));
    }

    @Test
    void getAllRequests_shouldReturnEmptyListWhenRequesterFindRequests() {
        userService.createUser(requester);
        User createdOwner = userService.createUser(itemOwner);
        ItemRequest createdItemRequest = itemRequestService.createRequest(itemRequestToCreate, 1L);
        Item item = Item.builder().name("testName").description("testDescription").itemRequest(createdItemRequest)
                .available(true).owner(createdOwner).build();
        itemRepository.save(item);

        var requestsList = itemRequestService.getAllRequests(1L, 0, 10);

        assertThat(requestsList, hasSize(0));
    }

    @Test
    void getRequestById() {
        userService.createUser(requester);
        User createdOwner = userService.createUser(itemOwner);
        ItemRequest createdItemRequest = itemRequestService.createRequest(itemRequestToCreate, 1L);
        Item item = Item.builder().name("testName").description("testDescription").itemRequest(createdItemRequest)
                .available(true).owner(createdOwner).build();
        itemRepository.save(item);

        ItemRequest itemRequest = itemRequestService.getRequestById(1L, 1L);

        assertThat(itemRequest.getId(), equalTo(1L));
    }
}
