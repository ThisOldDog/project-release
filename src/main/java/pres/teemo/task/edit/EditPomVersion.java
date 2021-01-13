package pres.teemo.task.edit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pres.teemo.data.StageParameter;
import pres.teemo.task.Task;
import pres.teemo.task.TaskCreator;
import pres.teemo.task.TaskDeclaration;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EditPomVersion implements TaskCreator {
    private static final Pattern TAG_PATTERN = Pattern.compile("<[^> !]+>");

    @Override
    public TaskDeclaration taskDeclaration() {
        return TaskDeclaration.EDIT_POM_VERSION;
    }

    @Override
    public Task createTaskInstance(StageParameter stageParameter) {
        return dataFlow -> {
            Project parentProject = editPomVersion(Paths.get(dataFlow.getLocalRepositoryStoreDirectory(), dataFlow.getProject().getProjectName(), "pom.xml"),
                    stageParameter.getParentVersion(),
                    stageParameter.getVersion());
            if (parentProject != null && !CollectionUtils.isEmpty(parentProject.getModules())) {
                logResult("项目包含子模块 : {}", dataFlow.getProject().getProjectName());
                for (String module : parentProject.getModules().get(0).getModule().split(",")) {
                    if ("ALL".equals(stageParameter.getModule()) || Objects.equals(stageParameter.getModule(), module)) {
                        editPomVersion(Paths.get(dataFlow.getLocalRepositoryStoreDirectory(), dataFlow.getProject().getProjectName(), module, "pom.xml"),
                                stageParameter.getParentVersion(),
                                stageParameter.getVersion());
                    }
                }
            }
        };
    }

    private Project editPomVersion(Path mvnRepository, String parentVersion, String version) throws Exception {
        logExecute("检查Pom仓库是否存在 : {}", mvnRepository);
        if (!Files.exists(mvnRepository, LinkOption.NOFOLLOW_LINKS)) {
            logError("代码仓库中没有找到 pom.xml 文件 : {}", mvnRepository);
            return null;
        } else {
            logExecute("解析Pom文件 : {}", mvnRepository);
            Project project = resolvePom(mvnRepository);
            if (project == null) {
                logError("解析Pom文件错误 : {}", mvnRepository);
            } else {
                logResult("解析到Pom文件 : parent.version [{}], version [{}]",
                        project.getParent() != null ? project.getParent().getVersion() : null, project.getVersion());
                if (Objects.equals(project.getParent() != null ? project.getParent().getVersion() : null, parentVersion)
                        && Objects.equals(project.getVersion(), version)) {
                    logExecute("目标Pom版本与当前Pom版本一致，跳过修改 : parent.version [{}], version [{}]", parentVersion, version);
                    return project;
                }
                logExecute("读取Pom文件，调整文件版本 : {}", mvnRepository);
                List<String> pomLines = Files.readAllLines(mvnRepository, StandardCharsets.UTF_8);
                List<String> tempPomLines = new ArrayList<>();
                Stack<String> nodeStack = new Stack<>();
                nodeStack.push("project");
                boolean changed = false;
                for (String pomLine : pomLines) {
                    Matcher matcher = TAG_PATTERN.matcher(pomLine);
                    while (matcher.find()) {
                        String tag = matcher.group();
                        // 自闭标签
                        if (tag.endsWith("/>")) {
                            continue;
                        }
                        // 闭合标签
                        if (tag.startsWith("</")) {
                            String tagName = tag.substring(2, tag.length() - 1);
                            if (Objects.equals(nodeStack.peek(), tagName)) {
                                nodeStack.pop();
                            } else {
                                logError("解析Pom标签时发生错误 : {}", tagName);
                            }
                            continue;
                        }
                        // 开始标签
                        nodeStack.push(tag.substring(1, tag.length() - 1));
                    }

                    if (pomLine.contains("<version>") && pomLine.contains("</version>")) {
                        if (Objects.equals(nodeStack.peek(), "parent")) {
                            changed = true;
                            tempPomLines.add(pomLine.substring(0, pomLine.indexOf("<version>")) + "<version>" + parentVersion + "</version>");
                            logResult("调整Pom文件Parent版本 : {} -> {}", pomLine.trim(), tempPomLines.get(tempPomLines.size() - 1).trim());
                        } else if (Objects.equals(nodeStack.peek(), "project") && StringUtils.hasText(version)) {
                            changed = true;
                            tempPomLines.add(pomLine.substring(0, pomLine.indexOf("<version>")) + "<version>" + version + "</version>");
                            logResult("调整Pom文件版本 : {} -> {}", pomLine.trim(), tempPomLines.get(tempPomLines.size() - 1).trim());
                        } else if (Objects.equals(nodeStack.peek(), "project")) {
                            changed = true;
                            logResult("删除Pom文件版本 : {} -> {}", pomLine.trim());
                        } else {
                            tempPomLines.add(pomLine);
                        }
                    } else {
                        tempPomLines.add(pomLine);
                    }
                }
                if (!changed) {
                    logResult("文件没有发生任何变化 : {}", mvnRepository);
                    return project;
                }
                logExecute("写出调整后的内容到目标文件 : {}", mvnRepository);
                Files.write(mvnRepository, tempPomLines, StandardCharsets.UTF_8,
                        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.SYNC);
                logResult("写出成功 : {}", mvnRepository);
            }
            return project;
        }
    }

    private Project resolvePom(Path pomFile) {
        try {
            return XmlMapper.builder()
                    .defaultUseWrapper(false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
                    .build()
                    .readValue(pomFile.toFile(), Project.class);
        } catch (Exception e) {
            logger.debug("非法的 Pom.xml.", e);
            return null;
        }
    }

    static class Project {
        private Parent parent;

        private String groupId;
        private String artifactId;
        private String version;

        private List<Module> modules;

        public Parent getParent() {
            return parent;
        }

        public Project setParent(Parent parent) {
            this.parent = parent;
            return this;
        }

        public String getGroupId() {
            return groupId;
        }

        public Project setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public Project setArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public Project setVersion(String version) {
            this.version = version;
            return this;
        }

        public List<Module> getModules() {
            return modules;
        }

        public Project setModules(List<Module> modules) {
            this.modules = modules;
            return this;
        }
    }

    static class Parent {
        private String groupId;
        private String artifactId;
        private String version;

        public String getGroupId() {
            return groupId;
        }

        public Parent setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public Parent setArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public Parent setVersion(String version) {
            this.version = version;
            return this;
        }
    }

    static class Module {
        private String module;

        public String getModule() {
            return module;
        }

        public Module setModule(String module) {
            if (this.module == null) {
                this.module = module;
            } else {
                this.module += ("," + module);
            }
            return this;
        }
    }
}
