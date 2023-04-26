package pcd.assignment1.jpf.pool;

import pcd.assignment1.jpf.tasks.Task;

public interface CustomThreadPool {
    void shutDown();

    boolean isShutDown();

    void execute(Task task) /*throws InterruptedException*/;

    void removeWorkerFromPool();

    void joinAllWorkers();
}
