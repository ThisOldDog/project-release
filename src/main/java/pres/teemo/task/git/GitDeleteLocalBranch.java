package pres.teemo.task.git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GitDeleteLocalBranch implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_DELETE_LOCAL_BRANCH;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("删除本地分支 : {}", stageParameter.getTargetBranchName());
            AtomicBoolean found = new AtomicBoolean(false);
            dataFlow.getLocalBranchList().forEach(localBranch -> {
                if (matchBranch(localBranch, stageParameter.getTargetBranchName())) {
                    try {
                        dataFlow.getGit()
                                .branchDelete()
                                .setBranchNames(localBranch.getName())
                                .setForce(true)
                                .call();
                        found.set(true);
                    } catch (GitAPIException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            if (found.get()) {
                logResult("删除本地分支成功 : {}", stageParameter.getTargetBranchName());
            } else {
                logResult("没有找到本地分支 : {}", stageParameter.getTargetBranchName());
            }
        };
    }
}
