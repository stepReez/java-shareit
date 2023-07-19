package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getOtherUsersRequests(long userId, int from, int size);

    ItemRequestDto getOneRequest(long requestId, long userId);
}
