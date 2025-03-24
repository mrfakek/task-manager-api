package by.tms.taskmanagerapi.service;

import by.tms.taskmanagerapi.dto.account.AccountCreateDto;
import by.tms.taskmanagerapi.dto.account.AccountResponseDto;
import by.tms.taskmanagerapi.entity.Account;
import by.tms.taskmanagerapi.exceptions.AlreadyExistsException;
import by.tms.taskmanagerapi.exceptions.NotFoundException;
import by.tms.taskmanagerapi.mapper.AccountMapper;
import by.tms.taskmanagerapi.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
    }

    public AccountResponseDto create(AccountCreateDto accountCreateDto) {
        if (existByEmail(accountCreateDto.getEmail())) throw new AlreadyExistsException("Account already exists");
        Account account = new Account();
        account.setEmail(accountCreateDto.getEmail());
        account.setPassword(new BCryptPasswordEncoder(11).encode(accountCreateDto.getPassword()));
        account = accountRepository.save(account);
        return accountMapper.toAccountResponseDto(account);
    }

    public boolean existByEmail(String email) {
     return accountRepository.existsByEmail(email);
    }

    public AccountResponseDto getCurrentAccount(Authentication authentication) {
        Account account = accountRepository.findByEmail(authentication.getName()).orElseThrow(() -> new NotFoundException("Account not found"));
        return accountMapper.toAccountResponseDto(account);
    }

    public Page<AccountResponseDto> getAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(accountMapper::toAccountResponseDto);
    }

    public AccountResponseDto updateAccount(AccountCreateDto accountCreateDto, Authentication authentication) {
        Account account = accountRepository.findByEmail(authentication.getName()).orElseThrow(() -> new NotFoundException("Account not found"));
         account = accountMapper.updateAccount(accountCreateDto, account);
         account = accountRepository.save(account);
         return accountMapper.toAccountResponseDto(account);
    }

    public void deleteById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
        accountRepository.delete(account);
    }

    public void delete(Authentication authentication) {
        Account account = accountRepository.findByEmail(authentication.getName()).orElseThrow(() -> new NotFoundException("Account not found"));
        accountRepository.delete(account);
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isPresent()) {
            var acc = account.get();
            return User
                    .withUsername(acc.getEmail())
                    .password(acc.getPassword())
                    .roles(acc.getRole().name())
                    .build();
        }
        throw new UsernameNotFoundException(email);
    }
}
