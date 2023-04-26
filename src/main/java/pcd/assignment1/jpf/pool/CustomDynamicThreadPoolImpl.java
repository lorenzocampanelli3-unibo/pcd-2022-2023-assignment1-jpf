package pcd.assignment1.jpf.pool;

import gov.nasa.jpf.vm.Verify;
import pcd.assignment1.jpf.tasks.Task;
import pcd.assignment1.jpf.util.Flag;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomDynamicThreadPoolImpl implements CustomThreadPool {

    private final Lock lock = new ReentrantLock();
    private int maxPoolSize;

    private int totalNumOfWorkersSpawned;
    private int numOfGhostWorkers;
    private BlockingQueue<Task> tasksQueue;
    private Queue<WorkerAgent> workersQueue;
    private Flag stopFlag;

    public CustomDynamicThreadPoolImpl(final int maxPoolSize, Flag stopFlag) {
        this.maxPoolSize = maxPoolSize;
        this.totalNumOfWorkersSpawned = 0;
        this.numOfGhostWorkers = 0;
        this.tasksQueue = new LinkedBlockingQueue<>();
        this.workersQueue = new ConcurrentLinkedQueue<>();
        this.stopFlag = stopFlag;
    }

    @Override
    public void shutDown() {
//        this.isShutdownInitiated.set();
//        this.stopFlag.set(true);
    }


    @Override
    public boolean isShutDown() {
        return this.stopFlag.isSet();
    }

    @Override
    public void execute(Task task) /*throws InterruptedException*/ {
        if (!this.isShutDown()) {
            boolean spawnNewAgent = false;
            this.lock.lock();
            try {
                int actualMaxPoolSize = maxPoolSize + numOfGhostWorkers;
                if (this.totalNumOfWorkersSpawned < actualMaxPoolSize) { // we have enough room to spawn a new agent
                    spawnNewAgent = true;
                    this.totalNumOfWorkersSpawned++;
                }
            } finally {
                this.lock.unlock();
            }
            Verify.beginAtomic();
            if (spawnNewAgent) {
                WorkerAgent workerAgent = new WorkerAgent(this.totalNumOfWorkersSpawned - 1, task, this.tasksQueue, this);
                workersQueue.add(workerAgent);
                workerAgent.start();
            } else {
                tasksQueue.add(task);
            }
            Verify.endAtomic();
        } /*else {
            throw new InterruptedException("Thread Pool shutdown initiated, unable to execute task.");
        }*/
    }

    public void removeWorkerFromPool() {
        /* Instead of really removing the thread from the workersList, I'll leave it in the list and consider
        * it a "ghost" worker by incrementing the "numOfGhostWorkers" count. I think it's better not to mess up with the
        * list by removing workers when they die, especially if a "master" agent will be joining on all of them to
        * wait for their termination */
//        Log.log("In RemoveWorkerFromPool");
        this.lock.lock();
        try {
            this.numOfGhostWorkers++;
        } finally {
            this.lock.unlock();
        }
//        Log.log("Exit removeWorkerFromPool");
    }

    @Override
    public void joinAllWorkers() {
        WorkerAgent worker;
        while ((worker = this.workersQueue.poll()) != null) {
            try {
//                Log.log("Joining worker" + worker);
                worker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
