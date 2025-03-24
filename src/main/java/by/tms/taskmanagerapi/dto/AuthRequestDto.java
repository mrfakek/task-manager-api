package by.tms.taskmanagerapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO for Authentication")
public class AuthRequestDto {

    @Schema(description = "Email address of the user", example = "test@test.com")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Your password must be at least 8 characters long and include at least one uppercase letter," +
                    " one lowercase letter, one number, and one special character (@$!%*?&)")
    @Schema(description = "The password for the account, must meet complexity requirements", example = "Password123!")
    private String password;
}
