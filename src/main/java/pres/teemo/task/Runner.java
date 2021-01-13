package pres.teemo.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pres.teemo.config.RunnerProperties;
import pres.teemo.data.DataFlow;
import pres.teemo.data.project.Project;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class Runner {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);
    private final TaskCreateFactory taskCreateFactory;
    private final RunnerProperties runnerProperties;

    @Autowired
    public Runner(TaskCreateFactory taskCreateFactory,
                  RunnerProperties runnerProperties) {
        this.taskCreateFactory = taskCreateFactory;
        this.runnerProperties = runnerProperties;
    }

    public void run() {
        List<Project> runProjectList = runnerProperties.getProjectList()
                .stream()
                .filter(item ->
                        // 没有指定选择哪些项目 && （没有指定排除 || 排除项中不排除该项）
                        (CollectionUtils.isEmpty(runnerProperties.getSelectProject()) && (CollectionUtils.isEmpty(runnerProperties.getUnSelectProject()) || !runnerProperties.getUnSelectProject().contains(item.getProjectName())))
                                // (指定项目不为空 && 指定项目包含当前项) && （没有指定排除 || 排除项中不排除该项）
                                || ((!CollectionUtils.isEmpty(runnerProperties.getSelectProject()) && runnerProperties.getSelectProject().contains(item.getProjectName())) && (CollectionUtils.isEmpty(runnerProperties.getUnSelectProject()) || !runnerProperties.getUnSelectProject().contains(item.getProjectName())))
                )
                .collect(Collectors.toList());
        AtomicInteger index = new AtomicInteger();
        runProjectList.forEach(project -> {
            logger.info("");
            logger.info("--------------------------------------- {} ({}/{}) ---------------------------------------", project.getProjectName(), index.incrementAndGet(), runProjectList.size());
            DataFlow dataFlow = new DataFlow()
                    .setGitlabProjectPrefix(runnerProperties.getGitlabProjectPrefix())
                    .setPrivateToken(runnerProperties.getPrivateToken())
                    .setLocalRepositoryStoreDirectory(runnerProperties.getLocalRepositoryStoreDirectory())
                    .setMavenHome(runnerProperties.getMavenHome())
                    .setProject(project)
                    .initialize();
            runnerProperties.getStageFlow().forEach(stage -> {
                try {
                    TaskCreator taskCreator = taskCreateFactory.getTaskCreator(stage.getTask());
                    taskCreator.logStart();
                    taskCreator.createTaskInstance(stage.getParameter())
                            .execute(dataFlow);
                    taskCreator.logStop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            logger.info("--------------------------------------- {} ({}/{}) ---------------------------------------", project.getProjectName(), index.get(), runProjectList.size());
        });
    }
}
