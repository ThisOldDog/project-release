package pres.teemo.task.git;

import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

import java.util.stream.Collectors;

@Component
public class GitBranch implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_BRANCH;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("拉取目标仓库所有分支 : {}", dataFlow.getGit().getRepository().getDirectory().toPath());
            dataFlow.setLocalBranchList(dataFlow.getGit()
                    .branchList()
                    .call());
            logResult("拉取到目标仓库本地分支 : {} [{}]", dataFlow.getLocalBranchList().size(), dataFlow.getLocalBranchList()
                    .stream()
                    .map(ref -> ref.getName().substring(ref.getName().lastIndexOf("/") + 1))
                    .collect(Collectors.joining(", ")));
            dataFlow.setRemoteBranchList(dataFlow.getGit()
                    .branchList()
                    .setListMode(ListBranchCommand.ListMode.REMOTE)
                    .call());
            logResult("拉取到目标仓库远程分支 : {} [{}]", dataFlow.getRemoteBranchList().size(), dataFlow.getRemoteBranchList()
                    .stream()
                    .map(ref -> ref.getName().substring(ref.getName().lastIndexOf("/") + 1))
                    .collect(Collectors.joining(", ")));
        };
    }
}
