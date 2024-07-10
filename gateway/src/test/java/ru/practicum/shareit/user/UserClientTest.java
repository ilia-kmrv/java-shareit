package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(UserClient.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserClientTest {
    private final UserClient client;
    private final MockRestServiceServer server;
    private final ObjectMapper mapper;

    @Value("${shareit-server.url}")
    private String serverUrl;

    String userDtoString;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        userDtoString = mapper.writeValueAsString(UserDto.builder()
                .id(0L)
                .name("name")
                .email("user@email.com")
                .build());
    }

    @Test
    void postUser_whenInvoked_thenStatusIsOk() {
        UserDto userDto = UserDto.builder().build();
        this.server.expect(requestTo(serverUrl + "/users"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(userDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.client.postUser(userDto);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getUser_whenInvoked_thenStatusIsOk() {
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/users/0"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(userDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.client.getUser(userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getAllUsers_whenInvoked_thenStatusIsOk() {
        this.server.expect(requestTo(serverUrl + "/users"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess());

        ResponseEntity dto = this.client.getAllUsers();

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void patchUser_whenInvoked_thenStatusIsOk() {
        UserDto userDto = UserDto.builder().build();
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/users/0"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(userDtoString, MediaType.APPLICATION_JSON));

        ResponseEntity dto = this.client.patchUser(userDto, userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }

    @Test
    void deleteUser_whenInvoked_thenStatusIsOk() {
        long userId = 0L;
        this.server.expect(requestTo(serverUrl + "/users/0"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        ResponseEntity dto = this.client.deleteUser(userId);

        assertTrue(dto.getStatusCode().is2xxSuccessful());
    }
}