package by.tms.taskmanagerapi.mapper;

import by.tms.taskmanagerapi.dto.comment.CommentResponseDto;
import by.tms.taskmanagerapi.entity.Comment;
import by.tms.taskmanagerapi.repository.AccountRepository;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AccountRepository.class})
public interface CommentMapper {
    CommentResponseDto toCommentResponseDto(Comment comment);
}
