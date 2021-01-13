package pres.teemo.task.git;

import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

@Component
public class GitPull implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_PULL;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("拉取仓库当前分支最新代码 : {}", dataFlow.getGit().getRepository().getDirectory().toPath());
            dataFlow.getGit().pull()
                    .setCredentialsProvider(dataFlow.getCredentialsProvider())
                    .call();
            logResult("拉取成功");
        };
    }
}
