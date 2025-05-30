package by.tms.taskmanagerapi.repository;

import by.tms.taskmanagerapi.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndIssue_Id(Long id, Long issueId);
    Page<Comment> findByIssue_Id(Long issueId, Pageable pageable);
    boolean existsByIdAndAuthor_Email(Long id, String authorEmail);
    boolean existsByIdAndIssue_Id(Long id, Long issueId);
    void deleteByIdAndIssue_Id(Long id, Long issueId);
}
