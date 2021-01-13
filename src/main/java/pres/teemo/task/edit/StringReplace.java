package pres.teemo.task.edit;

import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskCreator;
import pres.teemo.task.TaskDeclaration;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Collectors;

@Component
public class StringReplace implements TaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.STRING_REPLACE;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            Path targetFilePath = Paths.get(dataFlow.getLocalRepositoryStoreDirectory(), dataFlow.getProject().getProjectName(), stageParameter.getFilePath());
            logExecute("获取目标文件 : {}", targetFilePath);
            if (!Files.exists(targetFilePath, LinkOption.NOFOLLOW_LINKS)) {
                logError("项目 {} 不存在目标文件 : {}", dataFlow.getProject().getProjectName(), targetFilePath);
                return;
            }
            if (!Files.isRegularFile(targetFilePath, LinkOption.NOFOLLOW_LINKS)) {
                logError("项目 {} 存在目标路径，但是该路径并不指向一个文件 : {}", dataFlow.getProject().getProjectName(), targetFilePath);
                return;
            }
            if (!Files.isReadable(targetFilePath)) {
                logError("项目 {} 存在目标文件，但是该文件不可读 : {}", dataFlow.getProject().getProjectName(), targetFilePath);
                return;
            }
            if (!Files.isWritable(targetFilePath)) {
                logError("项目 {} 存在目标文件，但是该文件不可写 : {}", dataFlow.getProject().getProjectName(), targetFilePath);
                return;
            }
            logExecute("读取文件，替换 {} -> {}", stageParameter.getSearchString(), stageParameter.getReplaceString());
            Files.write(targetFilePath, Files.readAllLines(targetFilePath, StandardCharsets.UTF_8)
                            .stream()
                            .map(line -> line.replace(stageParameter.getSearchString(), stageParameter.getReplaceString()))
                            .collect(Collectors.toList()),
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
            logResult("写入文件成功，替换 {} -> {}", stageParameter.getSearchString(), stageParameter.getReplaceString());
        };
    }
}
