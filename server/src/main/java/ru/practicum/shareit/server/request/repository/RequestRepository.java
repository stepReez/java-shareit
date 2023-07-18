package ru.practicum.shareit.server.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT i FROM ItemRequest AS i " +
            "join i.requester as u " +
            "WHERE u.id = ?1")
    List<ItemRequest> findByUser(Long userId);

    Page<ItemRequest> findAll(Pageable pageable);

    @Query("SELECT i FROM ItemRequest AS i " +
            "join i.requester as u " +
            "WHERE u.id != ?1")
    List<ItemRequest> findAllWhereRequesterNotOwner(Long ownerId, Pageable pageable);
}
