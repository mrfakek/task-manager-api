package by.tms.taskmanagerapi.mapper;

import by.tms.taskmanagerapi.dto.account.AccountCreateDto;
import by.tms.taskmanagerapi.dto.account.AccountResponseDto;
import by.tms.taskmanagerapi.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponseDto toAccountResponseDto(Account account);

    Account updateAccount(AccountCreateDto accountCreateDto, @MappingTarget Account account);
}
