package by.tms.taskmanagerapi.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO for creating a new Comment")
public class CommentCreateDto {
    @NotBlank(message = "content is required")
    @Schema(description = "Content of the comment", example = "This is a comment")
    private String content;
}
