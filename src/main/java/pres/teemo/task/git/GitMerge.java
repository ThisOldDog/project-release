package pres.teemo.task.git;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.merge.MergeStrategy;
import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitMerge implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_MERGE;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("获取目标合并分支 : {}", stageParameter.getTargetBranchName());
            List<Ref> targetBranch = dataFlow.getRemoteBranchList()
                    .stream()
                    .filter(branch -> matchBranch(branch, stageParameter.getTargetBranchName()))
                    .collect(Collectors.toList());
            if (targetBranch.size() != 1) {
                logError("没有找到目标分支 : {}", stageParameter.getTargetBranchName());
            } else {
                logExecute("找到目标分支，开始合并 : {}", stageParameter.getTargetBranchName());
                MergeResult call = dataFlow.getGit()
                        .merge()
                        .include(targetBranch.get(0))
                        .setStrategy(MergeStrategy.THEIRS)
                        .call();
                if (MergeResult.MergeStatus.CONFLICTING.equals(call.getMergeStatus())) {
                    logError("合并分支冲突 : {}", stageParameter.getTargetBranchName());
                } else {
                    logResult("合并分支成功 : {}", stageParameter.getTargetBranchName());
                }
            }
        };
    }
}
