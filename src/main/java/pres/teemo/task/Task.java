package pres.teemo.task;

import pres.teemo.data.DataFlow;

public interface Task {

    void execute(DataFlow dataFlow) throws Exception;
}
