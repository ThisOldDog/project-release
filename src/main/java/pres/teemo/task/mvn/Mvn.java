package pres.teemo.task.mvn;

import org.apache.maven.shared.invoker.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskCreator;
import pres.teemo.task.TaskDeclaration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;

@Component
public class Mvn implements TaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.MVN;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            Path mvnRepository = Paths.get(dataFlow.getLocalRepositoryStoreDirectory(), dataFlow.getProject().getProjectName(), "pom.xml");
            logExecute("检查Pom仓库是否存在 : {}", mvnRepository);
            if (!Files.exists(mvnRepository, LinkOption.NOFOLLOW_LINKS)) {
                logError("代码仓库中没有找到 pom.xml 文件 : {}", mvnRepository);
            } else {
                logResult("在代码仓库中找到目标 pom.xml 文件 : {}", mvnRepository);
                logExecute("执行Maven : {} {}", stageParameter.getGoal(), stageParameter.getOption());
                PrintStream printStream = getPrintStream(stageParameter.getLogPath(), dataFlow.getProject().getProjectName());
                InvocationResult invocationResult = new DefaultInvoker()
                        .setMavenHome(Paths.get(dataFlow.getMavenHome()).toFile())
                        .setLogger(new PrintStreamLogger(printStream, InvokerLogger.WARN))
                        .setOutputHandler(new PrintStreamHandler(printStream, true))
                        .execute(new DefaultInvocationRequest()
                                .setPomFile(mvnRepository.toFile())
                                .setGoals(stageParameter.getGoal())
                                .setDebug(stageParameter.isDebug())
                                .setProperties(new Properties() {{
                                    stageParameter.getOption().forEach(this::setProperty);
                                }}));
                if (invocationResult.getExitCode() == 0) {
                    logResult("执行Maven成功");
                } else {
                    logError("执行Maven失败 : {}", invocationResult.getExecutionException() != null ? invocationResult.getExecutionException().getMessage() : "请查看Maven执行日志");
                }
            }
        };
    }

    private PrintStream getPrintStream(String logPath, String projectName) throws IOException {
        if (StringUtils.hasText(logPath)) {
            Path mavenLogDirectory = Paths.get(logPath);
            if (!Files.exists(mavenLogDirectory, LinkOption.NOFOLLOW_LINKS)) {
                logExecute("日志输出目录不存在，创建目标目录 : {}", logPath);
                mavenLogDirectory = Files.createDirectories(mavenLogDirectory);
            }
            Path logFile = mavenLogDirectory.resolve(projectName + ".log");
            if (Files.exists(logFile, LinkOption.NOFOLLOW_LINKS)) {
                logExecute("存在日志文件缓存，删除缓存文件 : {}", logFile);
                Files.deleteIfExists(logFile);
            }
            logResult("输出日志到文件 : {}", logFile);
            return new PrintStream(new FileOutputStream(logFile.toFile()), true, "GBK");
        }
        return System.out;
    }
}
