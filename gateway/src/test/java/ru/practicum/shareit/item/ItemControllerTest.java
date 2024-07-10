package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Header;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    @MockBean
    private final ItemClient itemClient;

    @BeforeEach
    void setUp() {
    }

    @SneakyThrows
    @Test
    void postItem_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        ItemDto dto = ItemDto.builder()
                .id(0L)
                .name("name")
                .description("description")
                .available(true)
                .ownerId(0L)
                .requestId(0L)
                .build();
        Long ownerId = 0L;
        when(itemClient.postItem(any(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemClient).postItem(dto, ownerId);
    }

    @SneakyThrows
    @Test
    void postItem_whenItemDtoHasBlankName_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        ItemDto dto = ItemDto.builder()
                .id(0L)
                .name(" ")
                .description("description")
                .available(true)
                .ownerId(0L)
                .requestId(0L)
                .build();
        Long ownerId = 0L;

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postItem(dto, ownerId);
    }

    @SneakyThrows
    @Test
    void postItem_whenItemDtoHasBlankDescription_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        ItemDto dto = ItemDto.builder()
                .id(0L)
                .name("name")
                .description(" ")
                .available(true)
                .ownerId(0L)
                .requestId(0L)
                .build();
        Long ownerId = 0L;

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postItem(dto, ownerId);
    }

    @SneakyThrows
    @Test
    void postItem_whenItemDtoAvailableIsNull_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        ItemDto dto = ItemDto.builder()
                .id(0L)
                .name("name")
                .description("description")
                .available(null)
                .ownerId(0L)
                .requestId(0L)
                .build();
        Long ownerId = 0L;

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postItem(dto, ownerId);
    }

    @SneakyThrows
    @Test
    void getItem_whenInvoked_StatusIsOkAndClientMethodCalled() {
        Long itemId = 0L;
        Long userId = 0L;
        when(itemClient.getItem(itemId, userId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemClient).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        when(itemClient.getAllItems(userId, from, size)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/items")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(itemClient).getAllItems(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenFromIsNegative_thenStatusIsAndClientMethodCalledWithDefaultValues() {
        Long userId = 0L;
        Integer from = -1;
        Integer size = 10;

        mvc.perform(get("/items")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItems(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenSizeIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = -10;

        mvc.perform(get("/items")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItems(userId, from, size);
    }

    @SneakyThrows
    @Test
    void patchItem_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long itemId = 0L;
        ItemDto dto = ItemDto.builder()
                .id(0L)
                .name("name")
                .description("description")
                .available(true)
                .ownerId(0L)
                .requestId(0L)
                .build();
        Long ownerId = 0L;
        when(itemClient.patchItem(dto, itemId, ownerId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemClient).patchItem(dto, itemId, ownerId);
    }

    @SneakyThrows
    @Test
    void deleteItem_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        Long itemId = 0L;
        Long userId = 0L;
        when(itemClient.getItem(itemId, userId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemClient).deleteItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void searchItems_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        String text = "search";
        Integer from = 0;
        Integer size = 10;
        Long userId = 0L;
        when(itemClient.searchItems(text, userId, from, size))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(itemClient).searchItems(text, userId, from, size);
    }

    @SneakyThrows
    @Test
    void searchItems_whenFromIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        String text = "search";
        Integer from = -1;
        Integer size = 10;
        Long userId = 0L;

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItems(text, userId, from, size);
    }

    @SneakyThrows
    @Test
    void searchItems_whenSizeIsNegative_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        String text = "search";
        Integer from = 0;
        Integer size = -10;
        Long userId = 0L;

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItems(text, userId, from, size);
    }

    @SneakyThrows
    @Test
    void postComment_whenInvoked_thenStatusIsOkAndClientMethodCalled() {
        CommentDto commentDto = CommentDto.builder().text("text").build();
        Long userId = 0L;
        Long itemId = 0L;
        when(itemClient.postComment(commentDto, itemId, userId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemClient).postComment(commentDto, itemId, userId);
    }

    @SneakyThrows
    @Test
    void postComment_whenCommentTextIsBlank_thenStatusIsBadRequestAndClientMethodNeverCalled() {
        CommentDto commentDto = CommentDto.builder().build();
        Long userId = 0L;
        Long itemId = 0L;

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemClient, never()).postComment(commentDto, itemId, userId);
    }
}
