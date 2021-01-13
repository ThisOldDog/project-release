package pres.teemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pres.teemo.data.DataContext;
import pres.teemo.data.Stage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "project.release")
public class RunnerProperties extends DataContext {
    private Set<String> selectProject = new HashSet<>();
    private Set<String> unSelectProject = new HashSet<>();
    private List<Stage> stageFlow;

    public Set<String> getSelectProject() {
        return selectProject;
    }

    public RunnerProperties setSelectProject(Set<String> selectProject) {
        this.selectProject = selectProject;
        return this;
    }

    public Set<String> getUnSelectProject() {
        return unSelectProject;
    }

    public RunnerProperties setUnSelectProject(Set<String> unSelectProject) {
        this.unSelectProject = unSelectProject;
        return this;
    }

    public List<Stage> getStageFlow() {
        return stageFlow;
    }

    public RunnerProperties setStageFlow(List<Stage> stageFlow) {
        this.stageFlow = stageFlow;
        return this;
    }
}
