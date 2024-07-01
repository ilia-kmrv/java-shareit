package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Util;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryIT {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(
                User.builder()
                        .name("name")
                        .email("user@email.com")
                        .build());

        itemRepository.save(Item.builder()
                .name("item name")
                .description("item dEscription")
                .available(true)
                .ownerId(1L)
                .requestId(1L)
                .build());

        itemRepository.save(Item.builder()
                .name("item2 name")
                .description("item2 description")
                .available(false)
                .ownerId(1L)
                .requestId(1L)
                .build());
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
    }

    @Test
    void search() {
        String text = "descriptioN";
        Pageable pageable = Util.page(0, 10);
        List<Item> items = itemRepository.search(text, pageable);

        assertEquals(1, items.size());
        assertEquals("item name", items.get(0).getName());
    }
}