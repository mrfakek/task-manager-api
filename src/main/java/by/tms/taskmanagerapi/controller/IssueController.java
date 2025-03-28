package by.tms.taskmanagerapi.controller;


import by.tms.taskmanagerapi.dto.comment.CommentCreateDto;
import by.tms.taskmanagerapi.dto.comment.CommentResponseDto;
import by.tms.taskmanagerapi.dto.issue.IssueCreateDto;
import by.tms.taskmanagerapi.dto.issue.IssueResponseDto;
import by.tms.taskmanagerapi.service.IssueService;
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
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new issue", responses = {
            @ApiResponse(description = "Issue created successfully", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IssueResponseDto.class))),
            @ApiResponse(description = "Invalid data provided", responseCode = "400"),
            @ApiResponse(description = "Forbidden, user does not have ADMIN role", responseCode = "403")
    })
    public ResponseEntity<IssueResponseDto> createIssue(@RequestBody @Valid IssueCreateDto issueCreateDto,
                                                   Authentication authentication) {
        IssueResponseDto saved = issueService.createIssue(issueCreateDto, authentication);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or @issueService.isUserAssignedToIssue(#issueId,authentication)")
    @GetMapping("/{issueId}")
    @Operation(summary = "Get an issue by its ID", description = "Fetches the details of an issue by its unique ID.",
            responses = {
                    @ApiResponse(description = "Issue retrieved successfully", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IssueResponseDto.class))),
                    @ApiResponse(description = "Issue not found", responseCode = "404"),
                    @ApiResponse(description = "Forbidden, user does not have required permissions", responseCode = "403")
            })
    public ResponseEntity<IssueResponseDto> getIssueById(@PathVariable("issueId") Long issueId) {
        IssueResponseDto issue = issueService.getIssueById(issueId);
        return new ResponseEntity<>(issue, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Operation(summary = "Get paginated list of issues", description = "Fetches a paginated list of issues, sorted by creation date (descending), with a default page size of 10.",
            responses = {
                    @ApiResponse(description = "Issues retrieved successfully", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                    @ApiResponse(description = "Forbidden, user does not have the required permissions", responseCode = "403")
            })
    public ResponseEntity<Page<IssueResponseDto>> getIssuePage(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<IssueResponseDto> page = issueService.getIssuePage(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @issueService.isUserAssignedToIssue(#issueId,authentication)")
    @PutMapping("/{issueId}")
    @Operation(summary = "Update an existing issue", description = "Updates the details of an issue identified by its ID. The user must have the 'ADMIN' role or be assigned to the issue.",
            responses = {
                    @ApiResponse(description = "Issue updated successfully", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IssueResponseDto.class))),
                    @ApiResponse(description = "Not found, the issue does not exist", responseCode = "404"),
                    @ApiResponse(description = "Forbidden, user does not have the required permissions", responseCode = "403"),
                    @ApiResponse(description = "Bad request, invalid input data", responseCode = "400")
            })
    public ResponseEntity<IssueResponseDto> updateIssue(@PathVariable("issueId") Long issueId,
                                                        @RequestBody @Valid IssueCreateDto issueCreateDto) {
        IssueResponseDto issueResponseDto = issueService.updateIssue(issueId, issueCreateDto);
        return new ResponseEntity<>(issueResponseDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @issueService.isUserAssignedToIssue(#issueId,authentication)")
    @PatchMapping("/{issueId}")
    @Operation(summary = "Partially update an existing issue", description = "Partially updates the details of an issue identified by its ID. The user must have the 'ADMIN' role or be assigned to the issue.",
            responses = {
                    @ApiResponse(description = "Issue updated successfully", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IssueResponseDto.class))),
                    @ApiResponse(description = "Not found, the issue does not exist", responseCode = "404"),
                    @ApiResponse(description = "Forbidden, user does not have the required permissions", responseCode = "403"),
                    @ApiResponse(description = "Bad request, invalid input data", responseCode = "400")
            })
    public ResponseEntity<IssueResponseDto> patchIssue(@PathVariable("issueId") Long issueId,
                                                       @RequestBody @Valid IssueCreateDto issueCreateDto) {
        IssueResponseDto issueResponseDto = issueService.patchIssue(issueId, issueCreateDto);
        return new ResponseEntity<>(issueResponseDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @issueService.isUserAssignedToIssue(#issueId,authentication)")
    @PostMapping("/{issueId}/comments")
    @Operation(summary = "Add a comment to an issue", description = "Adds a new comment to the specified issue. The user must have the 'ADMIN' role or be assigned to the issue.",
            responses = {
                    @ApiResponse(description = "Comment added successfully", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponseDto.class))),
                    @ApiResponse(description = "Not found, the issue does not exist", responseCode = "404",  content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Forbidden, user does not have the required permissions", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Bad request, invalid input data", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable("issueId") Long issueId,
                                                         @RequestBody @Valid CommentCreateDto commentCreateDto,
                                                         Authentication authentication) {
        CommentResponseDto commentResponseDto = issueService.addComment(issueId, commentCreateDto, authentication);
        return new ResponseEntity<>(commentResponseDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or @issueService.isUserAssignedToIssue(#issueId,authentication)")
    @GetMapping("/{issueId}/comments")
    @Operation(summary = "Get comments for an issue", description = "Retrieves a paginated list of comments for the specified issue. The user must have the 'ADMIN' role or be assigned to the issue.",
            responses = {
                    @ApiResponse(description = "Successfully retrieved comments", responseCode = "201", content = @Content(mediaType = "application/json",  schema = @Schema(implementation = Page.class))),
                    @ApiResponse(description = "Not found, the issue does not exist", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Forbidden, user does not have the required permissions", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<Page<CommentResponseDto>> getComments(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable("issueId") Long issueId,
            Authentication authentication) {
        Page<CommentResponseDto> page = issueService.getCommentsPage(issueId, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @PreAuthorize("@issueService.isAuthorComment(#commentId,authentication)")
    @PatchMapping("/{issueId}/comments/{commentId}")
    @Operation(summary = "Update a comment on a specific issue", description = "Updates the content of a comment for a specific issue. Only the author of the comment can update it.",
            responses = {
                    @ApiResponse(description = "Comment updated successfully", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponseDto.class))),
                    @ApiResponse(description = "Not found, the issue or comment does not exist", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Forbidden, user is not the author of the comment", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Bad request, invalid input data", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<CommentResponseDto> patchComment(@PathVariable("issueId") Long issueId,
                                                           @PathVariable("commentId") Long commentId,
                                                           @RequestBody @Valid CommentCreateDto commentCreateDto) {
        CommentResponseDto commentResponseDto = issueService.patchComment(issueId, commentId, commentCreateDto);
        return new ResponseEntity<>(commentResponseDto, HttpStatus.OK);
    }

    @PreAuthorize("@issueService.isAuthorComment(#commentId,authentication)")
    @DeleteMapping("/{issueId}/comments/{commentId}")
    @Operation(summary = "Delete a comment from a specific issue", description = "Deletes a comment from a specific issue. Only the author of the comment can delete it.",
            responses = {
                    @ApiResponse(description = "Comment deleted successfully", responseCode = "204"),
                    @ApiResponse(description = "Not found, the issue or comment does not exist", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Forbidden, user is not the author of the comment", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<Void> deleteComment(@PathVariable("issueId") Long issueId,
                                              @PathVariable("commentId") Long commentId) {
        issueService.deleteComment( commentId, issueId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("@issueService.isAuthorIssue(#issueId, authentication)")
    @DeleteMapping("/{issueId}")
    @Operation(summary = "Delete a specific issue", description = "Deletes a specific issue. Only the author of the issue can delete it.",
            responses = {
                    @ApiResponse(description = "Issue deleted successfully", responseCode = "204"),
                    @ApiResponse(description = "Not found, the issue does not exist", responseCode = "404",content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "Forbidden, user is not the author of the issue", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
            })
    public ResponseEntity<Void> deleteIssue(@PathVariable Long issueId) {
        issueService.deleteIssueById(issueId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}