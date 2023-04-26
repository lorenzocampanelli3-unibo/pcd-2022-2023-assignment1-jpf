package pcd.assignment1.jpf.pool;

import pcd.assignment1.jpf.tasks.Task;
import pcd.assignment1.jpf.util.Log;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class WorkerAgent extends Thread {
    // The queue of tasks that the Worker will monitor
    private BlockingQueue<Task> tasksQueue;
    // The Thread Pool that this Worker belongs to
    private CustomThreadPool threadPool;
    private Task firstTask;

    public WorkerAgent(int index, Task firstTask, BlockingQueue<Task> tasksQueue, CustomThreadPool threadPool) {
        super("DynamicThreadPool-Worker-" + index);
        this.firstTask = firstTask;
        this.tasksQueue = tasksQueue;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
//        boolean hasPickedUpAtLeastATask = false;
        if (!threadPool.isShutDown() && this.firstTask != null) {
//            Log.log(getName() + " started with a task.");
            this.firstTask.run();
            this.firstTask = null;
        }
//        try {
            while (!threadPool.isShutDown() && !tasksQueue.isEmpty()) {
                Task task;
                if ((task = tasksQueue.poll()) != null) {  // if not while
//                    hasPickedUpAtLeastATask = true;
                    task.run();
                }
//                Thread.sleep(50);
            }
        /*} catch (InterruptedException e) {
            throw new CustomThreadPoolException(e);
        }*/
        Log.log(getName() + " dying.");
//        Log.log(getName() + (hasPickedUpAtLeastATask ? " has picked up at least a task" : " has never picked up a task"));
        this.threadPool.removeWorkerFromPool();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerAgent that = (WorkerAgent) o;
        return tasksQueue.equals(that.tasksQueue) && threadPool.equals(that.threadPool) && Objects.equals(firstTask, that.firstTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasksQueue, threadPool, firstTask);
    }
}
