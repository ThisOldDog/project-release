package pres.teemo.task.git;

import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

@Component
public class GitAdd implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_ADD;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("将文件内容添加到索引 : {}", dataFlow.getGit().getRepository().getDirectory().toPath().getParent());
            dataFlow.getGit().add()
                    .addFilepattern(".")
                    .call();
            logResult("添加成功");
        };
    }
}
