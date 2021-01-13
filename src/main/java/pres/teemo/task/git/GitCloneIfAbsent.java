package pres.teemo.task.git;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskDeclaration;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class GitCloneIfAbsent implements GitTaskCreator {
    private static final String REMOTE_REPOSITORY_URL = "%s/%s.git";

    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_CLONE_IF_ABSENT;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            Path localRepositoryStoreDirectory = Paths.get(dataFlow.getLocalRepositoryStoreDirectory());
            logExecute("查找本地仓库存储目录 : {}", localRepositoryStoreDirectory);
            if (!Files.exists(localRepositoryStoreDirectory, LinkOption.NOFOLLOW_LINKS)) {
                logResult("本地仓库存储目录不存在，创建存储目录 : {}", localRepositoryStoreDirectory);
                Files.createDirectories(localRepositoryStoreDirectory);
            }
            Path localRepository = localRepositoryStoreDirectory.resolve(dataFlow.getProject().getProjectName())
                    .resolve(".git");
            logExecute("查找本地项目仓库 : {}", localRepository);
            if (Files.exists(localRepository, LinkOption.NOFOLLOW_LINKS)) {
                logResult("找到本地项目仓库，打开仓库 : {}", localRepository);
                dataFlow.setGit(Git.open(localRepository.toFile()));
            } else {
                logResult("未找到本地项目仓库，创建仓库目录 : {}", localRepository.getParent());
                Path localRepositoryRootDirectory = Files.createDirectories(localRepository.getParent());
                logExecute("Clone 远程仓库到本地仓库 : {}", localRepository.getParent());
                dataFlow.setGit(Git.cloneRepository()
                        .setDirectory(localRepositoryRootDirectory.toFile())
                        .setURI(String.format(REMOTE_REPOSITORY_URL, dataFlow.getGitlabProjectPrefix(), dataFlow.getProject().getProjectName()))
                        .setCloneAllBranches(true)
                        .setCredentialsProvider(dataFlow.getCredentialsProvider())
                        .call());
            }
        };
    }
}
