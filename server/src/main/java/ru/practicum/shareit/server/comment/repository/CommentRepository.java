package ru.practicum.shareit.server.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(long itemId);
}
