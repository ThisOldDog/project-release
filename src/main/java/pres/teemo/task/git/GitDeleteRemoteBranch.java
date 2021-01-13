package pres.teemo.task.git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class GitDeleteRemoteBranch implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_DELETE_REMOTE_BRANCH;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("删除远程分支 : {}", stageParameter.getTargetBranchName());
            AtomicBoolean found = new AtomicBoolean(false);
            dataFlow.getRemoteBranchList().forEach(remoteBranch -> {
                if (matchBranch(remoteBranch, stageParameter.getTargetBranchName())) {
                    try {
                        Iterable<PushResult> pushResults = dataFlow.getGit()
                                .push()
                                .setRefSpecs(new RefSpec()
                                        .setSource(null)
                                        .setDestination("refs/heads/" + stageParameter.getTargetBranchName()))
                                .setRemote("origin")
                                .setCredentialsProvider(dataFlow.getCredentialsProvider())
                                .call();
                        found.set(true);
                    } catch (GitAPIException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            if (found.get()) {
                logResult("删除远程分支成功 : {}", stageParameter.getTargetBranchName());
            } else {
                logResult("没有远程本地分支 : {}", stageParameter.getTargetBranchName());
            }
        };
    }
}
