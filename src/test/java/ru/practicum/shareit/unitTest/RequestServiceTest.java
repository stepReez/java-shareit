package ru.practicum.shareit.unitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BookingBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

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

    @Test
    void getOtherUsersRequestsBadRequestTest() {
        assertThrows(BookingBadRequestException.class,
                () -> requestService.getOtherUsersRequests(1, -10, 1));
    }
}
