package pres.teemo.task.edit;

import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskCreator;
import pres.teemo.task.TaskDeclaration;

import java.nio.file.*;

@Component
public class FileCopy implements TaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.FILE_COPY;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            Path sourceFile = Paths.get(stageParameter.getSourceFile());
            logExecute("获取源文件 : {}", sourceFile);
            if (!Files.exists(sourceFile, LinkOption.NOFOLLOW_LINKS)) {
                logError("源文件不存在 : {}", sourceFile);
                return;
            }
            if (!Files.isRegularFile(sourceFile, LinkOption.NOFOLLOW_LINKS)) {
                logError("指定源路径并不是一个文件 : {}", sourceFile);
                return;
            }
            if (!Files.isReadable(sourceFile)) {
                logError("存在源文件，但是该文件不可读 : {}", sourceFile);
                return;
            }
            Path targetFile = Paths.get(dataFlow.getLocalRepositoryStoreDirectory(), dataFlow.getProject().getProjectName(), stageParameter.getTargetFile());
            logExecute("复制源文件到目标文件：{} -> {}", sourceFile, Paths.get(dataFlow.getLocalRepositoryStoreDirectory()).relativize(targetFile));
            targetFile = Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS);
            logResult("复制文件成功：{} -> {}", sourceFile, Paths.get(dataFlow.getLocalRepositoryStoreDirectory()).relativize(targetFile));
        };
    }
}
