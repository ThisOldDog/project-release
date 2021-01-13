package pres.teemo.task.git;

import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

@Component
public class GitPush implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_PUSH;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("推送代码到远程仓库分支 : {}", stageParameter.getRemoteBranchName());
            dataFlow.getGit().push()
                    .setCredentialsProvider(dataFlow.getCredentialsProvider())
                    .add(stageParameter.getRemoteBranchName())
                    .setForce(true)
                    .call();
            logResult("推送成功");
        };
    }
}
