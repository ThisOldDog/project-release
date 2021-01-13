package pres.teemo.data;

import pres.teemo.data.project.Project;

import java.util.List;

public class DataContext {
    private String gitlabProjectPrefix;
    private String privateToken;
    private String localRepositoryStoreDirectory;
    private String mavenHome;
    private List<Project> projectList;

    public String getGitlabProjectPrefix() {
        return gitlabProjectPrefix;
    }

    public DataContext setGitlabProjectPrefix(String gitlabProjectPrefix) {
        this.gitlabProjectPrefix = gitlabProjectPrefix;
        return this;
    }

    public String getPrivateToken() {
        return privateToken;
    }

    public DataContext setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
        return this;
    }

    public String getLocalRepositoryStoreDirectory() {
        return localRepositoryStoreDirectory;
    }

    public DataContext setLocalRepositoryStoreDirectory(String localRepositoryStoreDirectory) {
        this.localRepositoryStoreDirectory = localRepositoryStoreDirectory;
        return this;
    }

    public String getMavenHome() {
        return mavenHome;
    }

    public void setMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public DataContext setProjectList(List<Project> projectList) {
        this.projectList = projectList;
        return this;
    }
}
