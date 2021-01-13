# Project Release

> 项目自动化发版工具

## 工具能力

- 对 gitlab 的Maven工程批量执行任务

### 支持的任务清单

任务分类 | 任务名称 | 任务描述
:--: | :--: | :--
Git | GitCloneIfAbsent | git clone command 或者存在本地缓存时直接打开
Git | GitBranch | 加载本地和远程所有分支
Git | GitCheckout | 切换到分支
Git | GitAdd | 将修改的问题提交到暂存区
Git | GitCommit | 提交暂存区的修改
Git | GitPush | 推送提交到远程分支
Git | GitDeleteLocalBranch | 删除本地分支
Git | GitDeleteRemoteBranch | 删除远程分支
Git | GitFetch | 从远程同步代码库
Git | GitMerge | 合并分支（**注意这里使用了策略 THEIRS**）
Git | GitPull | 拉去当前分支代码
Git | GitReset | 重置当前分支代码到HEAD
Git | GitTag | 生成本地Tag
Git | GitTagPush | 推送本地Tag到远程
文件编辑 | EditPomVersion | 修改 Pom 中 parent 的版本
文件编辑 | FileCopy | 拷贝文件到项目中，如果存在则会覆盖
文件编辑 | StringReplace | 指定字符串替换
Maven | Mvn | 执行Maven命令

### 任务参数

详见 [pres.teemo.data.StageParameter](src/main/java/pres/teemo/data/StageParameter.java)

### 自定义任务

1. 实现接口`pres.teemo.task.TaskCreator`。
2. 实现接口方法`pres.teemo.task.TaskCreator#taskDeclaration`，该方法返回一个任务名称的枚举类型，可以在`pres.teemo.task.TaskDeclaration`中定义。
3. 实现接口方法`pres.teemo.task.TaskCreator.createTaskInstance`，该方法返回一个任务实例，参数为配置文件中定义的任务参数，该类需要实现`pres.teemo.task.Task.execute`方法，方法参数为当前执行任务的数据流，推荐使用lambda表达式创建匿名内部类，代码比较简介。

以 GitPush 任务为例：
```java
@Component
public class GitPush implements GitTaskCreator {
    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.GIT_PUSH;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            logExecute("推送代码到远程仓库分支 : {}", stageParameter.getRemoteBranchName());
            // 如果执行了 GitCloneIfAbsent 任务，可以在数据流中获取到当前Git仓库
            dataFlow.getGit().push()
                    // 如果配置文件定义了授权信息，可以在数据流中获取到
                    .setCredentialsProvider(dataFlow.getCredentialsProvider())
                    // 获取配置文件中定义的任务参数
                    .add(stageParameter.getRemoteBranchName())
                    .setForce(true)
                    .call();
            logResult("推送成功");
        };
    }
}
```

## 使用指引

### 在 src/main/resources 下创建 application.yml

```yaml
project:
  release:
    gitlab-project-prefix: Gitlab 项目仓库前缀，例如：http://example.gitlab.com/{group name}
    private-token: 执行Git操作的用户Token，可以在Gitlab上创建：http://example.gitlab.com/profile/personal_access_tokens
    local-repository-store-directory: 本地仓库存储目录，例如：../temp/project-repository
    maven-home: Maven 所在目录，例如：C:\Program Files (x86)\apache-maven-3.6.1
    project-list:
      # 项目清单，如果项目之间有依赖顺序，被依赖的项目需要在依赖项目声明之前
      - project-name: project-parent
      - project-name: project-child01
      - project-name: project-child02
      - project-name: project-child01-grandson01
```

### 在 src/main/resources 下创建 application-{action}.yaml

action配置文件格式
```yaml
project:
  release:
    select-project:
      - 指定执行任务的项目列表
    un-select-project:
      - 指定排除执行任务的项目列表
    stage-flow:
      - task: 任务名称
        parameter:
          参数名称: 参数值
```

以application-release.yml 为例

```yaml
project:
  release:
    # 阶段定义
    stage-flow:
      # 下载或者打开仓库
      - task: git_clone_if_absent
      # 同步仓库
      - task: git_fetch
      # 拉取所有分支
      - task: git_branch
      # 切换到目标分支，如果目标分支已存在，则会直接切换，不存在则从原分支创建
      - task: git_checkout
        parameter:
          source-branch-name: 1.5-beta
          target-branch-name: 1.5-release
      # 合并目标分支到当前分支
      - task: git_merge
        parameter:
          target-branch-name: 1.5-beta
      # 编辑 pom 中 parent 的版本，可以自定子 module，ALL代表所有
      - task: edit_pom_version
        parameter:
          module: ALL
          parent-version: 1.5.0.RELEASE
      # 将修改添加到暂存区
      - task: git_add
      # 将暂存区的内容提交
      - task: git_commit
        parameter:
          commit-message: "[AUTO] 1.5.0.RELEASE"
      # 推送到远程分支
      - task: git_push
        parameter:
          remote-branch-name: 1.5-release
      # 执行 Maven 打包
      - task: mvn
        parameter:
          goal:
            - clean
            - package
            # - install
            - deploy
          option:
            maven.test.skip: true
            maven.javadoc.skip: true
            maven.springboot.skip: true
            maven.source.skip: true
            file.encoding: UTF-8
          log-path: src/main/resources/maven/log/1.5-release
```

### 启动程序

可以通过多种方式启动，比如通过环境变量、配置文件或者启动类参数，以启动类参数为例：

```java
@EnableConfigurationProperties(RunnerProperties.class)
@SpringBootApplication
public class ReleaseApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ReleaseApplication.class)
                .profiles("release")
                .run(args)
                .getBean(Runner.class)
                .run();
    }
}
```

### 其他常用配置

- 在action配置文件中指定部分项目执行任务：

```yaml
project:
  release:
    select-project:
      - project-01
      - project-02
```

- 在action配置文件中排除指定部分项目执行任务：

```yaml
project:
  release:
    un-select-project:
      - project-01
      - project-02
```
