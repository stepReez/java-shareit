package ru.practicum.shareit.server.comment.util;

import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.model.Comment;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.item.model.Item;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(commentDto.getCreated())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
