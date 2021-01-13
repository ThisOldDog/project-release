package pres.teemo.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskCreateFactory {
    private List<TaskCreator> taskCreators;

    @Autowired
    public TaskCreateFactory(List<TaskCreator> taskCreators) {
        this.taskCreators = taskCreators;
    }

    public TaskCreator getTaskCreator(TaskDeclaration taskDeclaration) {
        return taskCreators.stream()
                .filter(item -> item.taskDeclaration().equals(taskDeclaration))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
