package pres.teemo.task.git;

import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class GitCommit implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_COMMIT;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("提交项目修改 : {}", dataFlow.getGit().getRepository().getDirectory().toPath());
            dataFlow.getGit().commit()
                    .setMessage(stageParameter.getCommitMessage())
                    .call();
            logResult("提交成功");
        };
    }
}
