package pres.teemo.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pres.teemo.data.StageParameter;

public interface TaskCreator {
    Logger logger = LoggerFactory.getLogger(TaskCreator.class);

    TaskDeclaration taskDeclaration();

    Task createTaskInstance(StageParameter stageParameter);

    default void logStart() {
        logger.info("{} >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", taskDeclaration().getName());
    }

    default void logStop() {
        logger.info("{} <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", taskDeclaration().getName());
    }

    default void logExecute(String content, Object... parameters) {
        logger.info("{} >>> " + content, addFirst(taskDeclaration().getName(), parameters));
    }

    default void logResult(String content, Object... parameters) {
        logger.info("{} <<< " + content, addFirst(taskDeclaration().getName(), parameters));
    }

    default void logError(String content, Object... parameters) {
        logger.error("{} <<< " + content, addFirst(taskDeclaration().getName(), parameters));
    }

    default Object[] addFirst(Object first, Object[] objs) {
        Object[] newObjs = new Object[objs.length + 1];
        newObjs[0] = first;
        System.arraycopy(objs, 0, newObjs, 1, objs.length);
        return newObjs;
    }
}
