package by.tms.taskmanagerapi.dto.issue;

import by.tms.taskmanagerapi.dto.comment.CommentResponseDto;
import by.tms.taskmanagerapi.dto.account.AccountResponseDto;
import by.tms.taskmanagerapi.entity.Priority;
import by.tms.taskmanagerapi.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO representing the response for an Issue")
public class IssueResponseDto {

    @Schema(description = "Unique identifier for the issue", example = "1")
    private Long id;

    @Schema(description = "Title of the issue", example = "Bug in login form")
    private String title;

    @Schema(description = "Description of the issue", example = "The login form crashes when submitting with empty fields")
    private String description;

    @Schema(description = "Author of the issue")
    private AccountResponseDto author;

    @Schema(description = "Assigned user for the issue")
    private AccountResponseDto assignee;

    @Schema(description = "Current status of the issue",enumAsRef = true, example = "IN_REVIEW")
    private Status currentStatus;

    @Schema(description = "Priority of the issue", enumAsRef = true, example = "HIGH")
    private Priority priority;

    @Schema(description = "Date and time when the issue was created", example = "2025-03-23T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time when the issue was last updated", example = "2025-03-23T10:15:30")
    private LocalDateTime updatedAt;
}
