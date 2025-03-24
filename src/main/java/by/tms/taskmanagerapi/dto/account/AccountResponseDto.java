package by.tms.taskmanagerapi.dto.account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
public class AccountResponseDto {
    @Schema(description = "Unique identifier of the account", example = "1")
    private Long id;
    @Schema(description = "Email address of the account holder", example = "user@example.com")
    private String email;
}