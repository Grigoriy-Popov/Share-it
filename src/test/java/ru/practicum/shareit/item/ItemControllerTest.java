package ru.practicum.shareit.item;

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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH_ITEMS = "/items";
    private static final Long ITEM_FIRST_ID = 1L;

    User user = new User(1L, "testUser", "test@email.com");
    ItemDto itemDto = ItemDto.builder().id(ITEM_FIRST_ID).name("testName").description("testDescription")
            .available(true).requestId(1L).build();
    ItemDto itemDtoWithEmptyName = ItemDto.builder().name("").description("test").available(true).build();
    ItemDto itemDtoWithEmptyDescription = ItemDto.builder().name("test").description("").available(false).build();
    ItemDto itemDtoWithoutAvailable = ItemDto.builder().name("test").description("").build();
    List<ItemDto> itemsDtoList = List.of(
            new ItemDto(ITEM_FIRST_ID, "testName", "testName", true, 1L,
                    null, null, null),
            new ItemDto(ITEM_FIRST_ID + 1, "testName2", "testDescription2", true, 1L,
                    null, null, null));
    List<ItemDto> foundItemsDto = List.of(new ItemDto(ITEM_FIRST_ID, "testName", "testDescription",
            true, 1L, null, null, null));
    CommentDto commentDto = CommentDto.builder().id(1L).text("testText").authorName("testName").build();
    CommentDto invalidCommentDto = CommentDto.builder().id(1L).text("").authorName("testName").build();
    Item item = Item.builder().id(1L).owner(user).build();
    Comment comment = Comment.builder().id(1L).text("testText").item(item).author(user).build();

    @Test
    void createValidItem_shouldReturnJSONAndStatus200() throws Exception {
        when(itemService.createItem(any(), eq(ITEM_FIRST_ID)))
                .thenReturn(itemDto);
        mvc.perform(post(BASE_PATH_ITEMS)
                        .header(USER_ID_HEADER, user.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void createInvalidItemWithEmptyName_shouldReturnStatus400() throws Exception {
        when(itemService.createItem(any(), eq(ITEM_FIRST_ID)))
                .thenReturn(itemDtoWithEmptyName);
        mvc.perform(post(BASE_PATH_ITEMS)
                        .header(USER_ID_HEADER, user.getId())
                        .content(mapper.writeValueAsString(itemDtoWithEmptyName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createInvalidItemWithEmptyDescription_shouldReturnStatus400() throws Exception {
        when(itemService.createItem(any(), eq(ITEM_FIRST_ID)))
                .thenReturn(itemDtoWithEmptyDescription);
        mvc.perform(post(BASE_PATH_ITEMS)
                        .header(USER_ID_HEADER, user.getId())
                        .content(mapper.writeValueAsString(itemDtoWithEmptyDescription))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createInvalidItemWithoutAvailable_shouldReturnStatus400() throws Exception {
        when(itemService.createItem(any(), eq(ITEM_FIRST_ID)))
                .thenReturn(itemDtoWithoutAvailable);
        mvc.perform(post(BASE_PATH_ITEMS)
                        .header(USER_ID_HEADER, user.getId())
                        .content(mapper.writeValueAsString(itemDtoWithoutAvailable))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(get(BASE_PATH_ITEMS + "/{id}", ITEM_FIRST_ID)
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getAllUserItems() throws Exception {
        when(itemService.getAllUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemsDtoList);
        mvc.perform(get(BASE_PATH_ITEMS)
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(itemsDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(itemsDtoList.get(0).getName(),
                        itemsDtoList.get(1).getName())))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(itemsDtoList.get(0).getDescription(),
                        itemsDtoList.get(1).getDescription())));
    }

    @Test
    void editItem() throws Exception {
        when(itemService.editItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(patch(BASE_PATH_ITEMS + "/{id}", ITEM_FIRST_ID)
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void searchAvailableItemsByKeyword() throws Exception {
        when(itemService.searchAvailableItemsByKeyword(anyString(), anyInt(), anyInt()))
                .thenReturn(foundItemsDto);
        mvc.perform(get(BASE_PATH_ITEMS + "/search?text=TesT")
                        .header(USER_ID_HEADER, "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("testName")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("testDescription")));
    }

    @Test
    void addValidComment() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(comment);
        mvc.perform(post(BASE_PATH_ITEMS + "/1/comment")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthor().getName())));
    }

    @Test
    void addInvalidComment_shouldReturnStatus400() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(comment);
        mvc.perform(post(BASE_PATH_ITEMS + "/1/comment")
                        .header(USER_ID_HEADER, "1")
                        .content(mapper.writeValueAsString(invalidCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
