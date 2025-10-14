package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    boolean existsByStatusId(Long statusId);
    boolean existsByAssigneeId(Long assigneeId);
    @EntityGraph(attributePaths = {"labels", "status", "assignee"})
    List<Task> findAll();
    @EntityGraph(attributePaths = {"labels", "status", "assignee"})
    List<Task> findAll(Specification<Task> spec);
}
