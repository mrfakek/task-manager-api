package by.tms.taskmanagerapi.service;


import by.tms.taskmanagerapi.dto.comment.CommentCreateDto;
import by.tms.taskmanagerapi.dto.comment.CommentResponseDto;
import by.tms.taskmanagerapi.dto.issue.IssueCreateDto;
import by.tms.taskmanagerapi.dto.issue.IssueResponseDto;
import by.tms.taskmanagerapi.entity.*;
import by.tms.taskmanagerapi.repository.AccountRepository;
import by.tms.taskmanagerapi.repository.CommentRepository;
import by.tms.taskmanagerapi.repository.IssueRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class IssueServiceTest {

    @InjectMocks
    private final IssueService issueService;
    private final IssueRepository issueRepository;
    private final AccountRepository accountRepository;
    private final CommentRepository commentRepository;

    @Mock
    private Authentication authentication;
    private Account testAccount = new Account();
    private IssueCreateDto issueCreateDto = new IssueCreateDto();
    private Issue testIssue = new Issue();
    private String testEmail = "test@gmail.com";
    private String testPassword = "Password123!";
    private String title = "testTitle";
    private String testContent = "testContent";
    private String description = "testDescription";
    private Long idAsignee =1L;
    private Status currentStatus = Status.DONE;
    private Priority priority = Priority.HIGH;
    private int emailIndex = 1;

    @Autowired
    public IssueServiceTest(IssueService issueService,
                            IssueRepository issueRepository,
                            AccountRepository accountRepository,
                            CommentRepository commentRepository) {
        this.issueService = issueService;
        this.issueRepository = issueRepository;
        this.accountRepository = accountRepository;
        this.commentRepository = commentRepository;
    }

    private String generateEmail() {
        return "test" + (emailIndex++) + "@gmail.com";
    }
    @AfterAll
    void tearDown() {
        accountRepository.deleteAll();
    }

    @BeforeAll
    void setUp() {
        testAccount.setEmail(testEmail);
        testAccount.setPassword(testPassword);
        testAccount = accountRepository.save(testAccount);
        idAsignee = testAccount.getId();
        issueCreateDto.setTitle(title);
        issueCreateDto.setDescription(description);
        issueCreateDto.setCurrentStatus(currentStatus);
        issueCreateDto.setPriority(priority);
        testIssue.setAuthor(testAccount);
        testIssue.setTitle(title);
        testIssue.setDescription(description);
        testIssue.setCurrentStatus(currentStatus);
        testIssue.setPriority(priority);
        testIssue.setAuthor(testAccount);
        testIssue.setAssignee(testAccount);
        testIssue = issueRepository.save(testIssue);
    }

    @Test
    void createIssue() {
        when(authentication.getName()).thenReturn(testEmail);
        IssueResponseDto issueResponseDto = issueService.createIssue(issueCreateDto, authentication);
        Assertions.assertEquals(testIssue.getTitle(), issueResponseDto.getTitle());
        Assertions.assertEquals(testEmail, issueResponseDto.getAuthor().getEmail());
        Assertions.assertTrue(issueRepository.existsById(issueResponseDto.getId()));
    }

    @Test
    void isUserAssignedToIssue() {
        Account account = new Account();
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
        account = accountRepository.save(account);
        Issue issue = new Issue();
        issue.setAuthor(account);
        issue.setAssignee(account);
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setPriority(priority);
        issue = issueRepository.save(issue);
        when(authentication.getName()).thenReturn(account.getEmail());
        Assertions.assertTrue(issueService.isUserAssignedToIssue(issue.getId(),authentication));
        when(authentication.getName()).thenReturn(testEmail);
        Assertions.assertFalse(issueService.isUserAssignedToIssue(issue.getId(),authentication));
    }

    @Test
    void isAuthorIssue(){
        when(authentication.getName()).thenReturn(generateEmail());
        Assertions.assertFalse(issueService.isAuthorIssue(testIssue.getId(),authentication));
        when(authentication.getName()).thenReturn(testEmail);
        Assertions.assertTrue(issueService.isAuthorIssue(testIssue.getId(),authentication));
    }

    @Test
    void isAuthorComment(){
        Issue issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setAssignee(testAccount);
        issue.setTitle(title);
        issue.setDescription(description);
        issue = issueRepository.save(issue);
        Comment comment = new Comment();
        comment.setAuthor(testAccount);
        comment.setContent(testContent);
        comment.setIssue(issue);
        commentRepository.save(comment);
        when(authentication.getName()).thenReturn(generateEmail());
        Assertions.assertFalse(issueService.isAuthorComment(testIssue.getId(),authentication));
        when(authentication.getName()).thenReturn(testEmail);
        Assertions.assertTrue(issueService.isAuthorComment(testIssue.getId(),authentication));
    }
    
    @Test
    void getIssueById() {
       IssueResponseDto issueResponseDto = issueService.getIssueById(testIssue.getId());
        Assertions.assertEquals(testIssue.getTitle(), issueResponseDto.getTitle());
        Assertions.assertEquals(testEmail, issueResponseDto.getAuthor().getEmail());
        Assertions.assertEquals(testIssue.getId(), issueResponseDto.getId());
    }

    @Test
    void getIssuePage() {
        Issue issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issueRepository.save(issue);
        Pageable pageable = PageRequest.of(0, 3);
        Page<IssueResponseDto> page = issueService.getIssuePage(pageable);
        Assertions.assertEquals(3, page.getContent().size());
    }

    @Test
    void getIssuePageByAuthorId(){
        Account account = new Account();
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
       account = accountRepository.save(account);
        Issue issue = new Issue();
        issue.setAuthor(account);
        issue.setTitle(title);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(account);
        issue.setTitle(title);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(account);
        issue.setTitle(title);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(account);
        issue.setTitle(title);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(account);
        issue.setTitle(title);
        issueRepository.save(issue);
        Pageable pageable = PageRequest.of(0, 10);
        Page<IssueResponseDto> page = issueService.getIssuePageByAuthorId(account.getId(), pageable);
        Assertions.assertEquals(5, page.getContent().size());
    }

    @Test
    void getIssuePageByAssignedId(){
        Account account = new Account();
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
        account = accountRepository.save(account);
        Issue issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issue.setAssignee(account);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issue.setAssignee(account);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issue.setAssignee(account);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issue.setAssignee(account);
        issueRepository.save(issue);
        issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issue.setAssignee(account);
        issueRepository.save(issue);
        Pageable pageable = PageRequest.of(0, 10);
        Page<IssueResponseDto> page = issueService.getIssuePageByAssignedId(account.getId(), pageable);
        Assertions.assertEquals(5, page.getContent().size());
    }

    @Test
    void updateIssue() {
       Issue issue = new Issue();
       issue.setAuthor(testAccount);
       issue.setTitle("oldTitle");
       issue.setDescription("oldDescription");
       issue = issueRepository.save(issue);
       when(authentication.getName()).thenReturn(testEmail);
       IssueCreateDto issueCreateDto = new IssueCreateDto();
       issueCreateDto.setTitle("newTitle");
       Assertions.assertTrue(issueRepository.existsById(issue.getId()));
       Assertions.assertNotEquals(issueCreateDto.getTitle(), issue.getTitle());
       IssueResponseDto issueResponseDto = issueService.updateIssue(issue.getId(),issueCreateDto);
       Assertions.assertEquals(issueCreateDto.getTitle(),issueResponseDto.getTitle());
       Assertions.assertNull(issueResponseDto.getDescription());
    }

    @Test
    void patchIssue() {
        Issue issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle("oldTitle");
        issue.setDescription("oldDescription");
        issue = issueRepository.save(issue);
        when(authentication.getName()).thenReturn(testEmail);
        IssueCreateDto issueCreateDto = new IssueCreateDto();
        issueCreateDto.setTitle("newTitle");
        Assertions.assertTrue(issueRepository.existsById(issue.getId()));
        Assertions.assertNotEquals(issueCreateDto.getTitle(), issue.getTitle());
        IssueResponseDto issueResponseDto = issueService.patchIssue(issue.getId(),issueCreateDto);
        Assertions.assertEquals(issueCreateDto.getTitle(), issueResponseDto.getTitle());
        Assertions.assertNotNull(issueResponseDto.getDescription());
    }

    @Test
    void deleteIssueById() {
        Issue issue = new Issue();
        issue.setAssignee(testAccount);
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setPriority(priority);
        issue.setAuthor(testAccount);
        issue = issueRepository.save(issue);
        Assertions.assertTrue(issueRepository.existsById(issue.getId()));
        issueService.deleteIssueById(issue.getId());
        Assertions.assertFalse(issueRepository.existsById(issue.getId()));
    }

    @Test
    void addComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setContent("testContent");
        when(authentication.getName()).thenReturn(testEmail);
        Pageable pageable = PageRequest.of(0, 3);
        Page<CommentResponseDto> page = issueService.getCommentsPage(testIssue.getId(), pageable);
        Assertions.assertEquals(0, page.getContent().size());
        CommentResponseDto commentResponseDto = issueService.addComment(testIssue.getId(), commentCreateDto, authentication);
        page = issueService.getCommentsPage(testIssue.getId(), pageable);
        Assertions.assertEquals(1, page.getContent().size());
        Assertions.assertEquals(commentCreateDto.getContent(), commentResponseDto.getContent());
        Assertions.assertEquals(testIssue.getAuthor().getId(), commentResponseDto.getAuthor().getId());
    }

    @Test
    void getCommentsPage(){
        Issue issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issue.setDescription(description);
        issueRepository.save(issue);
        Comment comment = new Comment();
        comment.setAuthor(testAccount);
        comment.setContent(testContent);
        comment.setIssue(issue);
        commentRepository.save(comment);
        comment = new Comment();
        comment.setAuthor(testAccount);
        comment.setContent(testContent);
        comment.setIssue(issue);
        commentRepository.save(comment);
        comment = new Comment();
        comment.setAuthor(testAccount);
        comment.setContent(testContent);
        comment.setIssue(issue);
        commentRepository.save(comment);
        comment = new Comment();
        comment.setAuthor(testAccount);
        comment.setContent(testContent);
        comment.setIssue(issue);
        commentRepository.save(comment);
        comment = new Comment();
        comment.setAuthor(testAccount);
        comment.setContent(testContent);
        comment.setIssue(issue);
        commentRepository.save(comment);
        Pageable pageable = PageRequest.of(0, 3);
        Page<CommentResponseDto> page = issueService.getCommentsPage(issue.getId(), pageable);
        Assertions.assertEquals(3, page.getContent().size());
        pageable = PageRequest.of(0, 999);
        page = issueService.getCommentsPage(issue.getId(), pageable);
        Assertions.assertEquals(5, page.getContent().size());
    }

    @Test
    void patchComment(){
    Issue issue = new Issue();
    issue.setAuthor(testAccount);
    issue.setTitle(title);
    issue.setDescription(description);
    issue = issueRepository.save(issue);
    Comment comment = new Comment();
    comment.setAuthor(testAccount);
    comment.setContent(testContent);
    comment.setIssue(issue);
    comment = commentRepository.save(comment);
    Assertions.assertTrue(issueRepository.existsById(issue.getId()));
    Assertions.assertTrue(commentRepository.existsByIdAndIssue_Id(comment.getId(), issue.getId()));
    Assertions.assertEquals(testContent, comment.getContent());
    CommentCreateDto commentCreateDto = new CommentCreateDto();
    commentCreateDto.setContent("newContent");
    CommentResponseDto commentResponseDto = issueService.patchComment(issue.getId(),comment.getId(),commentCreateDto);
    Assertions.assertEquals("newContent", commentResponseDto.getContent());
    }

    @Test
    void deleteComment(){
        Issue issue = new Issue();
        issue.setAuthor(testAccount);
        issue.setTitle(title);
        issue.setDescription(description);
        issue = issueRepository.save(issue);
        Comment comment = new Comment();
        comment.setAuthor(testAccount);
        comment.setContent(testContent);
        comment.setIssue(issue);
        comment = commentRepository.save(comment);
        Pageable pageable = PageRequest.of(0, 3);
        Page<CommentResponseDto> page = issueService.getCommentsPage(issue.getId(), pageable);
        Assertions.assertEquals(1, page.getContent().size());
        issueService.deleteComment(comment.getId(),issue.getId());
        pageable = PageRequest.of(0, 3);
        page = issueService.getCommentsPage(issue.getId(), pageable);
        Assertions.assertEquals(0, page.getContent().size());
    }
}