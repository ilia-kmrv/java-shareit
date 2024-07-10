package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(ItemClient.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemClientTest {

    private final ItemClient itemClient;
    private final MockRestServiceServer server;
    private final ObjectMapper mapper;

    @Value("${shareit-server.url}")
    private String serverUrl;

    String itemDtoString;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        itemDtoString = mapper.writeValueAsString(ItemDto.builder()
                .id(0L)
                .name("name")
                .description("description")
                .available(true)
                .ownerId(0L)
                .requestId(0L)
                .build());
    }

    @Test
    void postItem_whenInvoked_thenStatusIsOk() {
        ItemDto itemDto = ItemDto.builder().build();
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/items"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(itemDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.itemClient.postItem(itemDto, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getItem_whenInvoked_thenStatusIsOk() {
        long itemId = 0L;
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/items/0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(itemDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.itemClient.getItem(itemId, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllItems_whenInvoked_thenStatusIsOk() {
        long userId = 0L;
        int from = 0;
        int size = 10;
        this.server.expect(requestTo(serverUrl + "/items?from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        ResponseEntity dto = this.itemClient.getAllItems(userId, from, size);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void patchItem_whenInvoked_thenStatusIsOk() {
        ItemDto itemDto = ItemDto.builder().build();
        long itemId = 0L;
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/items/0"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(itemDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.itemClient.patchItem(itemDto, itemId, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void deleteItem_whenInvoked_thenStatusIsOk() {
        long itemId = 0L;
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/items/0"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(itemDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.itemClient.deleteItem(itemId, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void searchItems() {
        long userId = 0L;
        int from = 0;
        int size = 10;
        String text = "text";
        this.server.expect(requestTo(serverUrl + "/items/search?text=text&from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        ResponseEntity dto = this.itemClient.searchItems(text, userId, from, size);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void postComment() {
        CommentDto commentDto = CommentDto.builder().build();
        long itemId = 0L;
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/items/0/comment"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        ResponseEntity dto = this.itemClient.postComment(commentDto, itemId, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }
}