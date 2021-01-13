package pres.teemo.data;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import pres.teemo.task.TaskDeclaration;

public class Stage {
    private TaskDeclaration task;
    @NestedConfigurationProperty
    private StageParameter parameter;

    public TaskDeclaration getTask() {
        return task;
    }

    public Stage setTask(TaskDeclaration task) {
        this.task = task;
        return this;
    }

    public StageParameter getParameter() {
        return parameter;
    }

    public Stage setParameter(StageParameter parameter) {
        this.parameter = parameter;
        return this;
    }

}
