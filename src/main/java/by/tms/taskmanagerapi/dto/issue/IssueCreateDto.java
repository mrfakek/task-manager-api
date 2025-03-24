package by.tms.taskmanagerapi.dto.issue;

import by.tms.taskmanagerapi.entity.Priority;
import by.tms.taskmanagerapi.entity.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO for creating a new Issue")
public class IssueCreateDto {

    @Schema(description = "The title of the Issue", example = "Fix bug in task creation API")
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Description of the issue", example = "There is a bug in the task creation API")
    private String description;

    @Schema(description = "ID of the assignee of the issue", example = "67890")
    private Long idAssignee;

    @Schema(description = "The current status of the issue", enumAsRef = true, example = "IN_PROGRESS")
    private Status currentStatus;

    @Schema(description = "Priority of the issue", enumAsRef = true, example = "HIGH" )
    private Priority priority;
}