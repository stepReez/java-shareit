package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(Long owner, Pageable pageable);

    List<Item> findByNameOrDescriptionContainingIgnoreCase(String query1, String query2, Pageable pageable);

    @Query("SELECT i FROM Item AS i " +
            "join i.request as r " +
            "where r.id = ?1")
    List<Item> findByRequest(Long request);
}
