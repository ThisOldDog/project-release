package pres.teemo.task.git;

import org.eclipse.jgit.transport.TagOpt;
import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

@Component
public class GitFetch implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_FETCH;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("拉取仓库最新信息 : {}", dataFlow.getGit().getRepository().getDirectory().toPath());
            dataFlow.getGit().fetch()
                    .setCredentialsProvider(dataFlow.getCredentialsProvider())
                    .setTagOpt(TagOpt.FETCH_TAGS) // -t
                    .setRemoveDeletedRefs(true)   // -p
                    .setForceUpdate(true)         // -f
                    .call();
            logResult("拉取成功");
        };
    }
}
