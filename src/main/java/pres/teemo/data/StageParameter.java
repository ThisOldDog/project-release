package pres.teemo.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageParameter {
    // Git checkout out
    private String sourceBranchName;
    private String targetBranchName;

    // Git Push
    private String remoteBranchName;

    // Git Commit
    private String commitMessage;

    // Git Tag
    private String tagName;

    // Maven
    private List<String> goal;
    private Map<String, String> option = new HashMap<>();
    private boolean debug;
    private String logPath;

    // Edit Pom Version
    private String module;
    private String parentVersion;
    private String version;
    // String Replace
    private String filePath;
    private String searchString;
    private String replaceString;
    // File Replace
    private String sourceFile;
    private String targetFile;

    public String getSourceBranchName() {
        return sourceBranchName;
    }

    public StageParameter setSourceBranchName(String sourceBranchName) {
        this.sourceBranchName = sourceBranchName;
        return this;
    }

    public String getTargetBranchName() {
        return targetBranchName;
    }

    public StageParameter setTargetBranchName(String targetBranchName) {
        this.targetBranchName = targetBranchName;
        return this;
    }

    public String getRemoteBranchName() {
        return remoteBranchName;
    }

    public StageParameter setRemoteBranchName(String remoteBranchName) {
        this.remoteBranchName = remoteBranchName;
        return this;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public StageParameter setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
        return this;
    }

    public String getTagName() {
        return tagName;
    }

    public StageParameter setTagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    public List<String> getGoal() {
        return goal;
    }

    public void setGoal(List<String> goal) {
        this.goal = goal;
    }

    public Map<String, String> getOption() {
        return option;
    }

    public void setOption(Map<String, String> option) {
        this.option = option;
    }

    public String getLogPath() {
        return logPath;
    }

    public StageParameter setLogPath(String logPath) {
        this.logPath = logPath;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public StageParameter setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getModule() {
        return module;
    }

    public StageParameter setModule(String module) {
        this.module = module;
        return this;
    }

    public String getParentVersion() {
        return parentVersion;
    }

    public StageParameter setParentVersion(String parentVersion) {
        this.parentVersion = parentVersion;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public StageParameter setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public StageParameter setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getSearchString() {
        return searchString;
    }

    public StageParameter setSearchString(String searchString) {
        this.searchString = searchString;
        return this;
    }

    public String getReplaceString() {
        return replaceString;
    }

    public StageParameter setReplaceString(String replaceString) {
        this.replaceString = replaceString;
        return this;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public StageParameter setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
        return this;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public StageParameter setTargetFile(String targetFile) {
        this.targetFile = targetFile;
        return this;
    }
}
