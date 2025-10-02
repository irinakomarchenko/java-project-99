package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByStatus_Id(Long statusId);
    boolean existsByAssignee_Id(Long assigneeId);
}
