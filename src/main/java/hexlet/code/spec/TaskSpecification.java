package hexlet.code.spec;

import hexlet.code.dto.TaskParamsDto;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    /**
     * Builds a composed specification based on filtering parameters.
     *
     * @param params DTO with filter parameters
     * @return combined specification for task filtering
     */
    public Specification<Task> build(TaskParamsDto params) {
        return Specification.allOf(
                titleContains(params.getTitleCont()),
                hasAssignee(params.getAssigneeId()),
                hasStatus(params.getStatus()),
                hasLabel(params.getLabelId())
        );
    }

    /**
     * Filters tasks whose title contains the specified substring (case-insensitive).
     *
     * @param title part of title to search for
     * @return specification for title filtering
     */
    private Specification<Task> titleContains(String title) {
        return (root, query, cb) ->
                title == null
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    /**
     * Filters tasks by assignee id.
     *
     * @param assigneeId the id of the assigned user
     * @return specification for assignee filtering
     */
    private Specification<Task> hasAssignee(Long assigneeId) {
        return (root, query, cb) ->
                assigneeId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    /**
     * Filters tasks by status slug.
     *
     * @param slug slug of the task status
     * @return specification for status filtering
     */
    private Specification<Task> hasStatus(String slug) {
        return (root, query, cb) ->
                slug == null
                        ? cb.conjunction()
                        : cb.equal(root.get("status").get("slug"), slug);
    }

    /**
     * Filters tasks by associated label id.
     *
     * @param labelId id of the label
     * @return specification for label filtering
     */
    private Specification<Task> hasLabel(Long labelId) {
        return (root, query, cb) ->
                labelId == null
                        ? cb.conjunction()
                        : cb.isMember(labelId, root.join("labels").get("id"));
    }
}

