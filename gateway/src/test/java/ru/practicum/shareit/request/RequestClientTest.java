package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(RequestClient.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestClientTest {

    private final RequestClient client;
    private final MockRestServiceServer server;
    private final ObjectMapper mapper;

    @Value("${shareit-server.url}")
    private String serverUrl;

    String requestDtoString;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        requestDtoString = mapper.writeValueAsString(ItemRequestDto.builder()
                .id(0L)
                .description("description")
                .build());
    }

    @Test
    void postItemRequest_whenInvoked_thenStatusIsOk() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/requests"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(requestDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.client.postItemRequest(itemRequestDto, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getRequestsByUser_whenInvoked_thenStatusIsOk() {
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/requests"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        ResponseEntity dto = this.client.getRequestsByUser(userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllRequests() {
        long userId = 0L;
        int from = 0;
        int size = 10;
        this.server.expect(requestTo(serverUrl + "/requests/all?from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        ResponseEntity dto = this.client.getAllRequests(from, size, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getRequest_whenInvoked_thenStatusIsOk() {
        long requestId = 0L;
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/requests/0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        ResponseEntity dto = this.client.getRequest(requestId, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }
}