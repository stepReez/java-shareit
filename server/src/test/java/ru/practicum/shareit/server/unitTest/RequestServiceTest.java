package ru.practicum.shareit.server.unitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.repository.RequestRepository;
import ru.practicum.shareit.server.request.service.RequestServiceImpl;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private RequestServiceImpl requestService;

    @BeforeEach
    public void init() {
        requestService = new RequestServiceImpl(requestRepository, userRepository, itemRepository);
    }

    @Test
    void createRequestUserNotExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(
                ItemRequestDto.builder().build(),
                1
        ));
    }

    @Test
    void getUserRequestsUserNotExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getUserRequests(1));
    }

    @Test
    void getOneRequestUserNotExist() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getOneRequest(1, 1));
    }
}
