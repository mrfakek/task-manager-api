package by.tms.taskmanagerapi.service;

import by.tms.taskmanagerapi.dto.comment.CommentCreateDto;
import by.tms.taskmanagerapi.dto.comment.CommentResponseDto;
import by.tms.taskmanagerapi.dto.issue.IssueCreateDto;
import by.tms.taskmanagerapi.dto.issue.IssueResponseDto;
import by.tms.taskmanagerapi.entity.Account;
import by.tms.taskmanagerapi.entity.Comment;
import by.tms.taskmanagerapi.entity.Issue;
import by.tms.taskmanagerapi.mapper.CommentMapper;
import by.tms.taskmanagerapi.mapper.IssueMapper;
import by.tms.taskmanagerapi.repository.AccountRepository;
import by.tms.taskmanagerapi.repository.CommentRepository;
import by.tms.taskmanagerapi.repository.IssueRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final IssueMapper issueMapper;
    private final AccountRepository accountRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository,
                        CommentRepository commentRepository,
                        CommentMapper commentMapper,
                        IssueMapper issueMapper,
                        AccountRepository accountRepository
                        ) {

        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.issueMapper = issueMapper;
        this.accountRepository = accountRepository;
    }

    public IssueResponseDto createIssue(IssueCreateDto issueCreateDto, Authentication authentication) {
        Issue issue = issueMapper.toIssue(issueCreateDto, accountRepository);
        Account author = accountRepository.findByEmail(authentication.getName()).orElseThrow(()->new EntityNotFoundException("Account not found"));
        issue.setAuthor(author);
        issue = issueRepository.save(issue);
        return issueMapper.toIssueResponseDto(issue);
    }

    public boolean isUserAssignedToIssue(Long issueId, Authentication authentication) {
        return issueRepository.existsByIdAndAssigneeEmail(issueId, authentication.getName());
    }

    public boolean isAuthorIssue(Long issueId, Authentication authentication) {
        return issueRepository.existsByIdAndAuthor_Email(issueId, authentication.getName());
    }

    public boolean isAuthorComment(Long commentId, Authentication authentication) {
        return commentRepository.existsByIdAndAuthor_Email(commentId, authentication.getName());
    }

    public IssueResponseDto getIssueById(Long issueId) {
 Issue issue = issueRepository.findById(issueId).orElseThrow(()->new EntityNotFoundException("Issue not found"));
        return issueMapper.toIssueResponseDto(issue);
    }

    public Page<IssueResponseDto> getIssuePage(Pageable pageable) {
        return issueRepository.findAll(pageable).map(issueMapper::toIssueResponseDto);
    }

    public IssueResponseDto updateIssue(Long issueId, IssueCreateDto issueCreateDto) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(()->new EntityNotFoundException("Issue not found"));
        issue = issueMapper.updateIssue(issueCreateDto, issue, accountRepository);
        return issueMapper.toIssueResponseDto(issue);
    }

    public void deleteIssueById(Long issueId) {
        if (!issueRepository.existsById(issueId)) throw new EntityNotFoundException("Issue not found");
        issueRepository.deleteById(issueId);
    }

    public IssueResponseDto patchIssue(Long issueId, @Valid IssueCreateDto issueCreateDto) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(()->new EntityNotFoundException("Issue not found"));
        issue = issueMapper.patchIssue(issueCreateDto, issue, accountRepository);
        return issueMapper.toIssueResponseDto(issue);
    }

    public CommentResponseDto addComment(Long issueId, CommentCreateDto commentCreateDto, Authentication authentication) {
        Account author = accountRepository.findByEmail(authentication.getName()).orElseThrow(()->new EntityNotFoundException("Account not found"));
        Issue issue = issueRepository.findById(issueId).orElseThrow(()->new EntityNotFoundException("Issue not found"));
        Comment comment = new Comment();
        comment.setContent(commentCreateDto.getContent());
        comment.setAuthor(author);
        comment.setIssue(issue);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(comment);
    }

    public Page<CommentResponseDto> getCommentsPage(Long issueId, Pageable pageable) {
        return commentRepository.findByIssue_Id(issueId, pageable).map(commentMapper::toCommentResponseDto);
    }

    public CommentResponseDto patchComment(Long issueId, Long commentId, CommentCreateDto commentCreateDto) {
        Comment comment = commentRepository.findByIdAndIssue_Id(commentId, issueId).orElseThrow(()->new EntityNotFoundException("Comment not found"));
        comment.setContent(commentCreateDto.getContent());
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(comment);
    }

    public void deleteComment( Long commentId,Long issueId) {
       if(!commentRepository.existsByIdAndIssue_Id(commentId, issueId)){
           throw new EntityNotFoundException("Comment not found");
       }
        commentRepository.deleteByIdAndIssue_Id(commentId, issueId);
    }
}