package ru.practicum.shareit.server.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.request.util.RequestMapper;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.exception.BookingBadRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.item.util.ItemMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.repository.RequestRepository;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId) {
        itemRequestDto.setRequesterId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
        ItemRequest itemRequest = requestRepository.save(RequestMapper.toItemRequest(
                itemRequestDto,
                userRepository.findById(userId).get()));
        log.info("Request with id = {} created", itemRequest.getId());
        return RequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not fond", userId));
        }
        List<ItemRequest> itemRequests = requestRepository.findByUser(userId);
        List<ItemRequestDto> itemRequestsDto = itemRequests.stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestsDto.forEach(request -> request.setItems(itemRepository.findByRequest(request.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList())));
        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequestDto> itemRequests = requestRepository.findAllWhereRequesterNotOwner(userId,
                        pageable).stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequests.forEach(request -> request.setItems(itemRepository.findByRequest(request.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList())));
        log.info("Requests found");
        return itemRequests;
    }

    @Override
    public ItemRequestDto getOneRequest(long requestId, long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User with id = %d not fond", userId));
        }
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id = %d not found", requestId)));
        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findByRequest(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        log.info("Request with id = {} found", requestId);
        return itemRequestDto;
    }
}
