package pres.teemo.task.git;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

@Component
public class GitCheckout implements GitTaskCreator {
    private GitPull gitPull;

    @Autowired
    public GitCheckout(GitPull gitPull) {
        this.gitPull = gitPull;
    }

    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_CHECKOUT;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("查询本地仓库是否存在目标分支 : {}", stageParameter.getTargetBranchName());
            if (dataFlow.getLocalBranchList().stream().anyMatch(branch -> matchBranch(branch, stageParameter.getTargetBranchName()))) {
                logResult("本地仓库存在目标分支 : {}", stageParameter.getTargetBranchName());
                logExecute("切换到目标分支 : {}", stageParameter.getTargetBranchName());
                dataFlow.getGit()
                        .checkout()
                        .setCreateBranch(false)
                        .setName(stageParameter.getTargetBranchName())
                        .call();
                logResult("已切换到本地目标分支 : {}", stageParameter.getTargetBranchName());
                gitPull.createTaskInstance(stageParameter).execute(dataFlow);
            } else {
                logResult("本地仓库不存在目标分支 : {}", stageParameter.getTargetBranchName());
                logExecute("查询远程仓库是否存在目标分支 : {}", stageParameter.getTargetBranchName());
                if (dataFlow.getRemoteBranchList().stream().anyMatch(branch -> matchBranch(branch, stageParameter.getTargetBranchName()))) {
                    logResult("远程仓库存在目标分支 : {}", stageParameter.getTargetBranchName());
                    logExecute("拉取远程仓库分支到本地 : {}", stageParameter.getTargetBranchName());
                    dataFlow.getGit()
                            .checkout()
                            .setCreateBranch(true)
                            .setName(stageParameter.getTargetBranchName())
                            .setStartPoint("origin/" + stageParameter.getTargetBranchName())
                            .call();
                    logResult("从远程分支创建本地目标分支 : {}", stageParameter.getTargetBranchName());
                } else {
                    logResult("远程仓库不存在目标分支 : {}", stageParameter.getTargetBranchName());
                    logExecute("查询本地仓库是否存在源分支 : {}", stageParameter.getSourceBranchName());
                    if (dataFlow.getLocalBranchList().stream().anyMatch(branch -> matchBranch(branch, stageParameter.getSourceBranchName()))) {
                        logResult("本地仓库存在源分支 : {}", stageParameter.getSourceBranchName());
                        logExecute("切换到本地仓库源分支 : {}", stageParameter.getSourceBranchName());
                        dataFlow.getGit()
                                .checkout()
                                .setCreateBranch(false)
                                .setName(stageParameter.getSourceBranchName());
                        logResult("已切换到本地源分支 : {}", stageParameter.getSourceBranchName());
                        gitPull.createTaskInstance(stageParameter).execute(dataFlow);
                        logExecute("切换到源分支到目标分支 : {} -> {}", stageParameter.getSourceBranchName(), stageParameter.getTargetBranchName());
                        dataFlow.getGit()
                                .checkout()
                                .setCreateBranch(true)
                                .setName(stageParameter.getTargetBranchName())
                                .call();
                        logResult("已切换到目标源分支 : {}", stageParameter.getTargetBranchName());
                    } else {
                        logResult("本地仓库不存在源分支 : {}", stageParameter.getSourceBranchName());
                        logExecute("查询远程仓库是否存在源分支 : {}", stageParameter.getSourceBranchName());
                        if (dataFlow.getRemoteBranchList().stream().anyMatch(branch -> matchBranch(branch, stageParameter.getSourceBranchName()))) {
                            logResult("远程仓库存在源分支 : {}", stageParameter.getSourceBranchName());
                            logExecute("拉取远程仓库分支到本地 : {}", stageParameter.getSourceBranchName());
                            dataFlow.getGit()
                                    .checkout()
                                    .setCreateBranch(true)
                                    .setName(stageParameter.getSourceBranchName())
                                    .setStartPoint("origin/" + stageParameter.getSourceBranchName())
                                    .call();
                            logResult("从远程分支创建本地源分支 : {}", stageParameter.getSourceBranchName());
                            logExecute("切换到源分支到目标分支 : {} -> {}", stageParameter.getSourceBranchName(), stageParameter.getTargetBranchName());
                            dataFlow.getGit()
                                    .checkout()
                                    .setCreateBranch(true)
                                    .setName(stageParameter.getTargetBranchName())
                                    .call();
                            logResult("已切换到目标源分支 : {}", stageParameter.getTargetBranchName());
                        } else {
                            logError("仓库不存在源分支，无法切换到目标分支 : {}", stageParameter.getSourceBranchName());
                        }
                    }
                }
            }
        };
    }
}
