package by.tms.taskmanagerapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task priority")
public enum Priority {

   @Schema(description = "No priority")
   NO_PRIORITY,

   @Schema(description = "Low priority")
   LOW,

   @Schema(description = "Medium priority")
   MEDIUM,

   @Schema(description = "High priority")
   HIGH;
}
