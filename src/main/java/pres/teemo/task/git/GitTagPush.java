package pres.teemo.task.git;

import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

@Component
public class GitTagPush implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_TAG_PUSH;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("推送本地 tag 到远程仓库");
            dataFlow.getGit().push()
                    .setCredentialsProvider(dataFlow.getCredentialsProvider())
                    .setPushTags()
                    .call();
            logResult("推送成功");
        };
    }
}
