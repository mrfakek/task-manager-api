package by.tms.taskmanagerapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task status")
public enum Status {
    @Schema(description = "Task is in the backlog")
    BACKLOG,

    @Schema(description = "Task is in progress")
    IN_PROGRESS,

    @Schema(description = "Task is under review")
    IN_REVIEW,

    @Schema(description = "Task is completed")
    DONE;
}
