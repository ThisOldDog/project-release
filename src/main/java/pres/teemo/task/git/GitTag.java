package pres.teemo.task.git;

import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

@Component
public class GitTag implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_TAG;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("{} 添加Tag : {}", dataFlow.getProject().getProjectName(), stageParameter.getTagName());
            dataFlow.getGit().tag()
                    .setName(stageParameter.getTagName())
                    .call();
            logResult("添加成功");
        };
    }
}
