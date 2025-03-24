package by.tms.taskmanagerapi.controller;


import by.tms.taskmanagerapi.dto.account.AccountCreateDto;
import by.tms.taskmanagerapi.dto.account.AccountResponseDto;
import by.tms.taskmanagerapi.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @Operation(summary = "Create a new account", responses = {
            @ApiResponse(description = "Account created successfully", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(description = "Bad request, invalid input data", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(description = "Account already exists", responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
    })
    public ResponseEntity<AccountResponseDto> createAccount(@RequestBody @Valid AccountCreateDto accountCreateDto) {
        AccountResponseDto saved = accountService.create(accountCreateDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping()
    @Operation(summary = "Get list of accounts", description = "Fetches a paginated list of accounts, sorted by creation date.",
            responses = {
                    @ApiResponse(description = "Successful retrieval of accounts list", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<Page<AccountResponseDto>> getAccountsList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AccountResponseDto> page = accountService.getAccounts(pageable);
        return new ResponseEntity<>(page,HttpStatus.OK);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current account details", responses = {
            @ApiResponse(description = "Account details", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponseDto.class)))
    })
    public ResponseEntity<AccountResponseDto> getCurrentAccount(Authentication authentication) {
        AccountResponseDto account = accountService.getCurrentAccount(authentication);
        return new ResponseEntity<>(account,HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Update account details", description = "Updates the account details for the currently authenticated user.",
            responses = {
                    @ApiResponse(description = "Successful update of account details", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponseDto.class))),
                    @ApiResponse(description = "Unauthorized request, user is not authenticated or does not have permission", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Bad request, invalid input data", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<AccountResponseDto> updateAccount(@RequestBody @Valid AccountCreateDto accountCreateDto,
                                                            Authentication authentication) {
        AccountResponseDto account = accountService.updateAccount(accountCreateDto, authentication);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account by ID", description = "Deletes an account by its ID. Only accessible to users with 'ADMIN' role.",
            responses = {
                    @ApiResponse(description = "Successfully deleted account", responseCode = "204"),
                    @ApiResponse(description = "Account not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Unauthorized, user does not have ADMIN role", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<Void> deleteAccountById(@PathVariable Long id) {
        accountService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(summary = "Delete current account", description = "Deletes the currently authenticated user's account.",
            responses = {
                    @ApiResponse(description = "Successfully deleted account", responseCode = "204"),
                    @ApiResponse(description = "Unauthorized, user not authenticated", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Forbidden, account deletion not allowed", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<Void> deleteCurrentAccount(Authentication authentication) {
        accountService.delete(authentication);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
