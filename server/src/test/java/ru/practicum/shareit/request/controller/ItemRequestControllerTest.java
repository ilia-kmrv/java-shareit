package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Header;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    private final ObjectMapper mapper;

    private final MockMvc mvc;

    @MockBean
    private final ItemRequestService itemRequestService;

    ItemRequest itemRequest;
    User user;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(0L)
                .name("username")
                .email("username@email.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(0L)
                .requester(user)
                .description("request description")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .build();
    }

    @SneakyThrows
    @Test
    void postItemRequest_whenInvoked_thenStatusIsOkAndItemRequestDtoReturned() {
        Long userId = 0L;
        when(itemRequestService.addItemRequest(itemRequestDto, userId)).thenReturn(itemRequest);

        String result = mvc.perform(post("/requests")
                        .header(Header.USER_ID, userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDto), result);
    }

//    @SneakyThrows
//    @Test
//    void postItemRequest_whenDescriptionIsBlank_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Long userId = 0L;
//        itemRequestDto.setDescription(" ");
//        when(itemRequestService.addItemRequest(itemRequestDto, userId)).thenReturn(itemRequest);
//
//        mvc.perform(post("/requests")
//                        .header(Header.USER_ID, userId)
//                        .content(mapper.writeValueAsString(itemRequestDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        verify(itemRequestService, never()).addItemRequest(itemRequestDto, userId);
//    }

    @SneakyThrows
    @Test
    void getRequestsByUser_whenInvoked_thenStatusIsOkServiceMethodCalled() {
        Long userId = 0L;
        when(itemRequestService.getRequestsByUserId(userId)).thenReturn(List.of());

        mvc.perform(get("/requests")
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemRequestService).getRequestsByUserId(userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Integer from = 0;
        Integer size = 10;
        Long userId = 0L;
        when(itemRequestService.getAllRequests(from, size, userId)).thenReturn(List.of());

        mvc.perform(get("/requests/all")
                        .header(Header.USER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        verify(itemRequestService).getAllRequests(from, size, userId);
    }

//    @SneakyThrows
//    @Test
//    void getAllRequests_whenFromIsNegative_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Integer from = -1;
//        Integer size = 10;
//        Long userId = 0L;
//        when(itemRequestService.getAllRequests(from, size, userId)).thenReturn(List.of());
//
//        mvc.perform(get("/requests/all")
//                        .header(Header.USER_ID, userId)
//                        .param("from", String.valueOf(from))
//                        .param("size", String.valueOf(size)))
//                .andExpect(status().isBadRequest());
//
//        verify(itemRequestService, never()).getAllRequests(from, size, userId);
//    }

//    @SneakyThrows
//    @Test
//    void getAllRequests_whenSizeIsNegative_thenStatusIsBadRequestAndServiceMethodNeverCalled() {
//        Integer from = 1;
//        Integer size = -10;
//        Long userId = 0L;
//        when(itemRequestService.getAllRequests(from, size, userId)).thenReturn(List.of());
//
//        mvc.perform(get("/requests/all")
//                        .header(Header.USER_ID, userId)
//                        .param("from", String.valueOf(from))
//                        .param("size", String.valueOf(size)))
//                .andExpect(status().isBadRequest());
//
//        verify(itemRequestService, never()).getAllRequests(from, size, userId);
//    }

    @SneakyThrows
    @Test
    void getRequest_whenInvoked_thenStatusIsOkAndServiceMethodCalled() {
        Long requestId = 0L;
        Long userId = 0L;
        ItemRequestDtoWithItems dto = ItemRequestDtoWithItems.builder().build();
        when(itemRequestService.getRequest(requestId, userId)).thenReturn(dto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(Header.USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemRequestService).getRequest(requestId, userId);

    }
}