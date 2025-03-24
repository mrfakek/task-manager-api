package by.tms.taskmanagerapi.service;

import by.tms.taskmanagerapi.dto.account.AccountCreateDto;
import by.tms.taskmanagerapi.dto.account.AccountResponseDto;
import by.tms.taskmanagerapi.entity.Account;
import by.tms.taskmanagerapi.entity.Role;
import by.tms.taskmanagerapi.exceptions.AlreadyExistsException;
import by.tms.taskmanagerapi.exceptions.NotFoundException;
import by.tms.taskmanagerapi.repository.AccountRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class AccountServiceTest {
    @InjectMocks
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @Mock
    private Authentication authentication;
    private String testEmail = "test@gmail.com";
    private String testPassword = "Password123!";
    private int emailIndex = 1;
    @Autowired
    public AccountServiceTest(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    private String generateEmail() {
        return "test" + (emailIndex++) + "@gmail.com";
    }
    @AfterAll
    void tearDown() {
        accountRepository.deleteAll();
    }

    @BeforeAll
    void setUpAll() {

        Account account = new Account();
        account.setEmail(testEmail);
        account.setPassword(testPassword);
        accountRepository.save(account);
    }

    @Test
    void create() {
        AccountCreateDto accountCreateDto = new AccountCreateDto();
        accountCreateDto.setEmail(generateEmail());
        accountCreateDto.setPassword(testPassword);
        AccountResponseDto accountResponseDto = accountService.create(accountCreateDto) ;
        Assertions.assertNotNull(accountResponseDto.getId());
        Assertions.assertEquals(accountCreateDto.getEmail(), accountResponseDto.getEmail());
        Account account = accountRepository.findById(accountResponseDto.getId()).orElseThrow(()-> new NotFoundException("Account not found"));
        Assertions.assertNotEquals(account.getPassword(), accountCreateDto.getPassword());
        Assertions.assertEquals(account.getRole(), Role.USER);
        Assertions.assertTrue(accountRepository.existsById(accountResponseDto.getId()));
        Assertions.assertThrows(AlreadyExistsException.class,()->accountService.create(accountCreateDto));
    }

    @Test
    void existByEmail() {
        Assertions.assertTrue(accountService.existByEmail(testEmail));
        Assertions.assertFalse(accountService.existByEmail("another@gmail.com"));
    }

    @Test
    void getCurrentAccount() {
        when(authentication.getName()).thenReturn(testEmail);
    AccountResponseDto accountResponseDto = accountService.getCurrentAccount(authentication);
    Assertions.assertNotNull(accountResponseDto);
    Assertions.assertEquals(testEmail, accountResponseDto.getEmail());
    }

    @Test
    void getAccounts() {
        Account account = new Account();
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
        accountRepository.save(account);
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
        accountRepository.save(account);
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
        accountRepository.save(account);
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
        accountRepository.save(account);
        Pageable pageable = PageRequest.of(0, 3);
        Page<AccountResponseDto> page = accountService.getAccounts(pageable);
        Assertions.assertNotNull(page);
        Assertions.assertEquals(3, page.getContent().size());
    }

    @Test
    void updateAccount() {
       AccountCreateDto accountCreateDto = new AccountCreateDto();
        when(authentication.getName()).thenReturn(testEmail);
        accountCreateDto.setEmail(generateEmail());
        accountCreateDto.setPassword(testPassword);
        Assertions.assertFalse(accountRepository.existsByEmail(accountCreateDto.getEmail()));
        accountService.updateAccount(accountCreateDto,authentication);
        Assertions.assertTrue(accountRepository.existsByEmail(accountCreateDto.getEmail()));
    }

    @Test
    void deleteById() {
        Account account = new Account();
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
        account = accountRepository.save(account);
        Assertions.assertTrue(accountRepository.existsById(account.getId()));
        accountService.deleteById(account.getId());
        Assertions.assertFalse(accountRepository.existsById(account.getId()));
    }

    @Test
    void delete() {
        Account account = new Account();
        account.setEmail(generateEmail());
        account.setPassword(testPassword);
        when(authentication.getName()).thenReturn(account.getEmail());
      account = accountRepository.save(account);
        Assertions.assertTrue(accountRepository.existsByEmail(account.getEmail()));
        accountService.delete(authentication);
        Assertions.assertFalse(accountRepository.existsByEmail(account.getEmail()));
    }

    @Test
    void loadUserByUsername() {
        UserDetails userDetails = accountService.loadUserByUsername(testEmail);
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(testEmail, userDetails.getUsername());
        Assertions.assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }
}