package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Header;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private final ItemService itemService;

    Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(0L)
                .name("item name")
                .description("item description")
                .available(true)
                .ownerId(0L)
                .requestId(0L)
                .build();
    }

    @SneakyThrows
    @Test
    void postItem_whenInvoked_thenStatusIsOkAndItemDtoReturned() {
        ItemDto dto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
        Long ownerId = 0L;
        when(itemService.addItem(any(), anyLong())).thenReturn(item);

        String result = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(item), result);
    }

    @SneakyThrows
    @Test
    void postItem_whenItemDtoHasBlankName_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
        ItemDto dto = ItemDto.builder()
                .id(item.getId())
                .name(" ")
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
        Long ownerId = 0L;

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(item, ownerId);
    }

    @SneakyThrows
    @Test
    void postItem_whenItemDtoHasBlankDescription_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
        ItemDto dto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(" ")
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
        Long ownerId = 0L;

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(item, ownerId);
    }

    @SneakyThrows
    @Test
    void postItem_whenItemDtoAvailableIsNull_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
        ItemDto dto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(null)
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
        Long ownerId = 0L;

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(item, ownerId);
    }

    @SneakyThrows
    @Test
    void getItem_whenInvoked_StatusIsOkAndServiceMethodCalled() {
        Long itemId = 0L;
        Long userId = 0L;
        OwnerItemDto dto = OwnerItemDto.builder().build();
        when(itemService.getItem(itemId, userId)).thenReturn(dto);

        mvc.perform(get("/items/{itemId}", itemId)
                .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemService).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        when(itemService.getAllItems(userId, from, size)).thenReturn(List.of());

        mvc.perform(get("/items")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(itemService).getAllItems(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenFromIsNegative_thenStatusIsAndServiceMethodCalledWithDefaultValues() {
        Long userId = 0L;
        Integer from = -1;
        Integer size = 10;
        when(itemService.getAllItems(userId, from, size)).thenReturn(List.of());

        mvc.perform(get("/items")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItems(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenSizeIsNegative_thenStatusIsAndServiceMethodCalledWithDefaultValues() {
        Long userId = 0L;
        Integer from = 0;
        Integer size = -10;
        when(itemService.getAllItems(userId, from, size)).thenReturn(List.of());

        mvc.perform(get("/items")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItems(userId, from, size);
    }

    @SneakyThrows
    @Test
    void patchItem_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long itemId = 0L;
        ItemDto dto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
        Long ownerId = 0L;
        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(item);

        String result = mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, ownerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(item), result);
    }

    @SneakyThrows
    @Test
    void deleteItem_whenInvoked_thenStatusIsOk() {
        Long itemId = 0L;
        Long userId = 0L;
        OwnerItemDto dto = OwnerItemDto.builder().build();
        when(itemService.getItem(itemId, userId)).thenReturn(dto);

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void searchItems_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        String text = "search";
        Integer from = 0;
        Integer size = 10;
        when(itemService.searchItems(text, from, size)).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(itemService).searchItems(text, from, size);
    }

    @SneakyThrows
    @Test
    void searchItems_whenFromIsNegative_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
        String text = "search";
        Integer from = -1;
        Integer size = 10;
        when(itemService.searchItems(text, from, size)).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItems(text, from, size);
    }

    @SneakyThrows
    @Test
    void searchItems_whenSizeIsNegative_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
        String text = "search";
        Integer from = 0;
        Integer size = -10;
        when(itemService.searchItems(text, from, size)).thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItems(text, from, size);
    }

    @SneakyThrows
    @Test
    void postComment_whenInvoked_thenStatusIsOkAndCommentDtoReturned() {
        CommentDto commentDto = CommentDto.builder().text("text").build();
        Long userId = 0L;
        Long itemId = 0L;
        when(itemService.addComment(commentDto, itemId, userId)).thenReturn(commentDto);

        String result = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(commentDto), result);
    }

    @SneakyThrows
    @Test
    void postComment_whenCommentTextIsBlank_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
        CommentDto commentDto = CommentDto.builder().build();
        Long userId = 0L;
        Long itemId = 0L;
        when(itemService.addComment(commentDto, itemId, userId)).thenReturn(commentDto);

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

        verify(itemService, never()).addComment(commentDto, itemId, userId);
    }
}