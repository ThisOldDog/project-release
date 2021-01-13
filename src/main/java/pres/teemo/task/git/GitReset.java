package pres.teemo.task.git;

import org.eclipse.jgit.api.ResetCommand;
import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

@Component
public class GitReset implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_RESET;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("重置代码到指定分支 : {}", stageParameter.getTargetBranchName());
            dataFlow.getGit().reset()
                    .setMode(ResetCommand.ResetType.HARD)
                    .setRef("origin/" + stageParameter.getTargetBranchName())
                    .call();
            logResult("重置成功");
        };
    }
}
