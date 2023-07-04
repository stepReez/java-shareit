package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long id, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.booker as u " +
            "where u.id = ?1 AND b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findFutureByBooker(Long id, LocalDateTime localDateTime, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.booker as u " +
            "where u.id = ?1 AND b.start < ?2 AND b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentByBooker(Long id, LocalDateTime localDateTime, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.booker as u " +
            "where u.id = ?1 AND b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findPastByBooker(Long id, LocalDateTime localDateTime, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.booker as u " +
            "where u.id = ?1 AND status LIKE 'WAITING' " +
            "order by b.start desc")
    List<Booking> findByStateWaitingByBooker(Long id, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.booker as u " +
            "where u.id = ?1 AND status LIKE 'REJECTED' " +
            "order by b.start desc")
    List<Booking> findByStateRejectedByBooker(Long id, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.item as i " +
            "where i.owner = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwner(Long id, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.item as i " +
            "where i.owner = ?1 AND b.start < ?2 AND b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentByOwner(Long id, LocalDateTime localDateTime, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.item as i " +
            "where i.owner = ?1 AND b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findPastByOwner(Long id, LocalDateTime localDateTime, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.item as i " +
            "where i.owner = ?1 AND status LIKE ?2 " +
            "order by b.start desc")
    List<Booking> findStateByOwner(Long id, String state, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.item as i " +
            "where i.owner = ?1 AND b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findFutureByOwner(Long id, LocalDateTime localDateTime, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "join b.item as i " +
            "where i.id = ?1 AND b.start <= ?2 AND status LIKE 'APPROVED' " +
            "order by b.start desc")
    List<Booking> getLastBookingByItemId(Long id, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b " +
            "join b.item as i " +
            "where i.id = ?1 AND b.start > ?2 AND status LIKE 'APPROVED' " +
            "order by b.start")
    List<Booking> getNextBookingByItemId(Long id, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b " +
            "join b.item as i " +
            "join b.booker as u " +
            "where i.id = ?1 AND b.id = ?2")
    List<Booking> getBookingByItemAndUser(Long itemId, Long userId);
}
