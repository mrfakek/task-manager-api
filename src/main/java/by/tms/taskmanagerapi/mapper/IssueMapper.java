package by.tms.taskmanagerapi.mapper;

import by.tms.taskmanagerapi.dto.issue.IssueCreateDto;
import by.tms.taskmanagerapi.dto.issue.IssueResponseDto;
import by.tms.taskmanagerapi.entity.Account;
import by.tms.taskmanagerapi.entity.Issue;
import by.tms.taskmanagerapi.exceptions.NotFoundException;
import by.tms.taskmanagerapi.repository.AccountRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AccountRepository.class})
public interface IssueMapper {

    @Mapping(target = "assignee", expression = "java(toAccount(issueCreateDto.getIdAssignee(), accountRepository))")
    Issue toIssue(IssueCreateDto issueCreateDto, @Context AccountRepository accountRepository);
    IssueResponseDto toIssueResponseDto(Issue issue);

    @Mapping(target = "id", ignore = true)
    Issue updateIssue(IssueCreateDto issueCreateDto, @MappingTarget Issue issue, @Context AccountRepository accountRepository);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Issue patchIssue(IssueCreateDto issueCreateDto, @MappingTarget Issue issue, @Context AccountRepository accountRepository);

    default Account toAccount(Long id, @Context AccountRepository accountRepository) {
        if (id == null) {
            return null;
        }
return accountRepository.findById(id).orElseThrow(()->new NotFoundException("Assigned user not exist"));
    }
}
