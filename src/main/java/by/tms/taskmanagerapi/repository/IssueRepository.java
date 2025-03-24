package by.tms.taskmanagerapi.repository;

import by.tms.taskmanagerapi.entity.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    boolean existsByIdAndAssigneeEmail(Long issueId, String name);
    boolean existsByIdAndAuthor_Email(Long id, String authorEmail);
    Page<Issue> findAll(Pageable pageable);
}