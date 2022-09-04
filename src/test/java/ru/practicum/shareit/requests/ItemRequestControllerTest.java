package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH_REQUESTS = "/requests";
    User requester = new User(1L, "testUser", "test@email.com");
    ItemRequest itemRequestToCreate = ItemRequest.builder().description("testDescr").build();
    ItemRequest invalidItemRequestToCreate = ItemRequest.builder().description("").build();
    ItemRequest createdItemRequest = ItemRequest.builder().description("testDescr").requester(requester).build();
    ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(createdItemRequest);

    @Test
    void createValidRequest() throws Exception {
        when(itemRequestService.createRequest(any(), anyLong()))
                .thenReturn(createdItemRequest);
        mvc.perform(post(BASE_PATH_REQUESTS)
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemRequestToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    void createInvalidRequestWithEmptyDescription_shouldReturnStatus400() throws Exception {
        when(itemRequestService.createRequest(any(), anyLong()))
                .thenReturn(createdItemRequest);
        mvc.perform(post(BASE_PATH_REQUESTS)
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(invalidItemRequestToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUserRequests() throws Exception {
        when(itemRequestService.getAllUserRequests(anyLong()))
                .thenReturn(List.of(createdItemRequest));
        mvc.perform(get(BASE_PATH_REQUESTS)
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(createdItemRequest));
        mvc.perform(get(BASE_PATH_REQUESTS + "/all")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(createdItemRequest);
        mvc.perform(get(BASE_PATH_REQUESTS + "/1")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));
    }
}
