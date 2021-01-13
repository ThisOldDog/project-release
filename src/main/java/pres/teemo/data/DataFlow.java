package pres.teemo.data;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import pres.teemo.data.project.Project;

import java.util.List;

public class DataFlow {
    private String gitlabProjectPrefix;
    private String privateToken;
    private String localRepositoryStoreDirectory;
    private String mavenHome;
    private Project project;
    private Git git;
    private CredentialsProvider credentialsProvider;
    private List<Ref> localBranchList;
    private List<Ref> remoteBranchList;

    public DataFlow initialize() {
        credentialsProvider = new UsernamePasswordCredentialsProvider("PRIVATE-TOKEN", privateToken);
        return this;
    }

    public String getGitlabProjectPrefix() {
        return gitlabProjectPrefix;
    }

    public DataFlow setGitlabProjectPrefix(String gitlabProjectPrefix) {
        this.gitlabProjectPrefix = gitlabProjectPrefix;
        return this;
    }

    public String getPrivateToken() {
        return privateToken;
    }

    public DataFlow setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
        return this;
    }

    public String getLocalRepositoryStoreDirectory() {
        return localRepositoryStoreDirectory;
    }

    public DataFlow setLocalRepositoryStoreDirectory(String localRepositoryStoreDirectory) {
        this.localRepositoryStoreDirectory = localRepositoryStoreDirectory;
        return this;
    }

    public String getMavenHome() {
        return mavenHome;
    }

    public DataFlow setMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
        return this;
    }

    public Project getProject() {
        return project;
    }

    public DataFlow setProject(Project project) {
        this.project = project;
        return this;
    }

    public Git getGit() {
        return git;
    }

    public DataFlow setGit(Git git) {
        this.git = git;
        return this;
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public DataFlow setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public List<Ref> getLocalBranchList() {
        return localBranchList;
    }

    public DataFlow setLocalBranchList(List<Ref> localBranchList) {
        this.localBranchList = localBranchList;
        return this;
    }

    public List<Ref> getRemoteBranchList() {
        return remoteBranchList;
    }

    public DataFlow setRemoteBranchList(List<Ref> remoteBranchList) {
        this.remoteBranchList = remoteBranchList;
        return this;
    }
}
