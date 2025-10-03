package hexlet.code.app.spec;

import hexlet.code.app.dto.TaskParamsDto;
import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDto params) {
        return Specification.allOf(
                titleContains(params.getTitleCont()),
                hasAssignee(params.getAssigneeId()),
                hasStatus(params.getStatus()),
                hasLabel(params.getLabelId())
        );
    }

    private Specification<Task> titleContains(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("name")), "%" + title.toLowerCase() + "%");
    }

    private Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? null : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    private Specification<Task> hasStatus(String slug) {
        return (root, query, cb) ->
                slug == null ? null : cb.equal(root.get("status").get("slug"), slug);
    }

    private Specification<Task> hasLabel(Long labelId) {
        return (root, query, cb) ->
                labelId == null ? null : cb.isMember(labelId, root.join("labels").get("id"));
    }
}
