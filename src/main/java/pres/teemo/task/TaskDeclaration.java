package pres.teemo.task;

public enum TaskDeclaration {
    // Git
    GIT_CLONE_IF_ABSENT("Git Clone"),
    GIT_FETCH("Git Fetch"),
    GIT_PULL("Git Pull"),
    GIT_PUSH("Git Push"),
    GIT_BRANCH("Git Branch"),
    GIT_CHECKOUT("Git Checkout"),
    GIT_ADD("Git Add"),
    GIT_COMMIT("Git Commit"),
    GIT_MERGE("Git Merge"),
    GIT_DELETE_LOCAL_BRANCH("Git Delete Local Branch"),
    GIT_DELETE_REMOTE_BRANCH("Git Delete Remote Branch"),
    GIT_TAG("Git Tag"),
    GIT_TAG_PUSH("Git Tag Push"),
    GIT_RESET("Git Reset"),
    // Maven
    MVN("Mvn"),
    // Editor
    EDIT_POM_VERSION("Edit Pom Version"),
    STRING_REPLACE("String Replace"),
    FILE_COPY("File Copy");

    private final String name;

    TaskDeclaration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
