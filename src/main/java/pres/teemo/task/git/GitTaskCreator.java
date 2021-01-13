package pres.teemo.task.git;

import org.eclipse.jgit.lib.Ref;
import pres.teemo.task.TaskCreator;

public interface GitTaskCreator extends TaskCreator {
    default boolean matchBranch(Ref branch, String branchName) {
        return branch.getName().endsWith("/" + branchName);
    }
}
