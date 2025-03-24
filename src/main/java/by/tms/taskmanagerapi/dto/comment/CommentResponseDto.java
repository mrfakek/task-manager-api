package by.tms.taskmanagerapi.dto.comment;

import by.tms.taskmanagerapi.dto.account.AccountResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO representing the response for a Comment")
public class CommentResponseDto {

    @Schema(description = "Unique identifier for the comment", example = "1")
    private Long id;

    @Schema(description = "Content of the comment", example = "This is a comment")
    private String content;

    @Schema(description = "Author of the comment")
    private AccountResponseDto author;

    @Schema(description = "Date and time when the comment was created", example = "2025-03-23T12:25:18")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time when the comment was last updated", example = "2025-03-23T12:25:18")
    private LocalDateTime updatedAt;
}
