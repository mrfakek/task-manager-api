package by.tms.taskmanagerapi.service;


import by.tms.taskmanagerapi.dto.issue.IssueCreateDto;
import by.tms.taskmanagerapi.dto.issue.IssueResponseDto;
import by.tms.taskmanagerapi.entity.Account;
import by.tms.taskmanagerapi.entity.Issue;
import by.tms.taskmanagerapi.entity.Priority;
import by.tms.taskmanagerapi.entity.Status;
import by.tms.taskmanagerapi.repository.AccountRepository;
import by.tms.taskmanagerapi.repository.IssueRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Mock
    private Authentication authentication;
    private Account account = new Account();
    private IssueCreateDto issueCreateDto = new IssueCreateDto();
    private Issue issue = new Issue();
    private String testEmail = "test@gmail.com";
    private String testPassword = "Password123!";
    private String title = "testTitle";
    private String description = "testDescription";
    private Long idAsignee =1L;
    private Status currentStatus = Status.DONE;
    private Priority priority = Priority.HIGH;

    @Autowired
    public IssueServiceTest(IssueService issueService,
                            IssueRepository issueRepository,
                            AccountRepository accountRepository) {
        this.issueService = issueService;
        this.issueRepository = issueRepository;
        this.accountRepository = accountRepository;
    }

    @BeforeAll
    void setUp() {
        account.setEmail(testEmail);
        account.setPassword(testPassword);
        account = accountRepository.save(account);
        idAsignee = account.getId();
        issueCreateDto.setTitle(title);
        issueCreateDto.setDescription(description);
        issueCreateDto.setCurrentStatus(currentStatus);
        issueCreateDto.setPriority(priority);
        issue.setAuthor(account);
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setCurrentStatus(currentStatus);
        issue.setPriority(priority);
        issue.setAuthor(account);
        issue.setAssignee(account);
        issue = issueRepository.save(issue);
    }





    @Test
    void createIssue() {
        when(authentication.getName()).thenReturn(testEmail);
        IssueResponseDto issueResponseDto = issueService.createIssue(issueCreateDto, authentication);
        Assertions.assertEquals(issueResponseDto.getTitle(), issue.getTitle());
        Assertions.assertEquals(issueResponseDto.getAuthor().getEmail(), testEmail);
        Assertions.assertTrue(issueRepository.existsById(issueResponseDto.getId()));
    }

    @Test
    void isUserAssignedToIssue() {
        when(authentication.getName()).thenReturn(testEmail);
        Account testAccount = new Account();
        testAccount.setEmail("another@gmail.com");
        testAccount.setPassword(testPassword);
        testAccount = accountRepository.save(testAccount);
        Issue testIssue = new Issue();
        testIssue.setAuthor(testAccount);
        testIssue.setAssignee(account);
        testIssue.setTitle(title);
        testIssue.setDescription(description);
        testIssue.setPriority(priority);
        testIssue.setAssignee(testAccount);
        testIssue = issueRepository.save(testIssue);
        Assertions.assertTrue(issueService.isUserAssignedToIssue(issue.getId(),authentication));
        Assertions.assertFalse(issueService.isUserAssignedToIssue(testIssue.getId(),authentication));
    }
    
    @Test
    void getIssueById() {
       IssueResponseDto issueResponseDto = issueService.getIssueById(issue.getId());
        Assertions.assertEquals(issueResponseDto.getTitle(), issue.getTitle());
        Assertions.assertEquals(issueResponseDto.getAuthor().getEmail(), testEmail);
        Assertions.assertTrue(issueResponseDto.getId().equals(issue.getId()));
    }

    @Test
    void deleteIssueById() {
        Issue issue = new Issue();
        issue.setAssignee(account);
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setPriority(priority);
        issue.setAuthor(account);
        issue = issueRepository.save(issue);
        Assertions.assertTrue(issueRepository.existsById(issue.getId()));
        issueService.deleteIssueById(issue.getId());
        Assertions.assertFalse(issueRepository.existsById(issue.getId()));
    }
}