package by.tms.taskmanagerapi.repository;

import by.tms.taskmanagerapi.entity.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    boolean existsByIdAndAssignee_Email(Long issueId, String name);

    boolean existsByIdAndAuthor_Email(Long issueId, String authorEmail);

    Page<Issue> findAll(Pageable pageable);

    Page<Issue> findByAuthor_Id(Long id, Pageable pageable);

    Page<Issue> findByAssignee_Id(Long assigneeId, Pageable pageable);

}
