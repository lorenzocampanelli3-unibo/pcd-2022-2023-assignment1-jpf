package pcd.assignment1.jpf;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JPFVerificationMain {

    public static final int MAX_WORKERS = 2;

    public static class MockStats {

        private int totLinesRead;
        private int numOfAnalyzedDirs;
        private int numOfAnalyzedFiles;

        public MockStats() {
            this.totLinesRead = 0;
            this.numOfAnalyzedDirs = 0;
            this.numOfAnalyzedFiles = 0;
        }

        public synchronized void updateFileStats(int linesCount) {
            this.numOfAnalyzedFiles++;
            this.totLinesRead += linesCount;
        }

        public synchronized void updateDirStats() {
            this.numOfAnalyzedDirs++;
        }

        public synchronized int getNumOfAnalyzedDirs() {
            return numOfAnalyzedDirs;
        }

        public synchronized int getNumOfAnalyzedFiles() {
            return numOfAnalyzedFiles;
        }

        public synchronized int getTotLinesRead() {
            return totLinesRead;
        }
    }

    public static class ThreadPool {
        private Queue<Thread> workersQueue = new ConcurrentLinkedQueue<>();

        public void addAndStartWorker(Thread worker) {
            workersQueue.add(worker);
            worker.start();
        }

        public void joinAllWorkers() {
            Thread worker;
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

    public static class MockDirectoryListingTask implements Runnable {
        private ThreadPool pool;
        private List<List<Integer>> entries;
        private MockStats stats;

        public MockDirectoryListingTask(List<List<Integer>> entries, MockStats stats, ThreadPool pool) {
            this.pool = pool;
            this.entries = entries;
            this.stats = stats;
        }

        @Override
        public void run() {
            for (List<Integer> entry : this.entries) {
                if (entry.size() > 1) {
                    List<List<Integer>> subDir = new ArrayList<>();
                    for (Integer file : entry) {
                        subDir.add(Collections.singletonList(file));
                    }
                    this.pool.addAndStartWorker(new Thread(new MockDirectoryListingTask(subDir, this.stats, this.pool)));
                }
                else {
                    this.stats.updateFileStats(entry.get(0));
                }
            }
            this.stats.updateDirStats();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MockStats stats = new MockStats();
        ThreadPool pool = new ThreadPool();
        List<List<Integer>> rootDir = new ArrayList<>();
        rootDir.add(Arrays.asList(1, 2));
        rootDir.add(Arrays.asList(1, 2));
        rootDir.add(Collections.singletonList(1));
        rootDir.add(Collections.singletonList(1));

        pool.addAndStartWorker(new Thread(new MockDirectoryListingTask(rootDir, stats, pool)));
        pool.joinAllWorkers();

//        if (stats.getTotLinesRead() != 8) {
//            System.out.println("Error: should have read a total of 8 lines.");
//        } else {
//            System.out.println("Success: it has read a total of 8 lines");
//        }
//        System.out.println("# Files: " + stats.getNumOfAnalyzedFiles());
//        System.out.println("# Dirs: " + stats.getNumOfAnalyzedDirs());

        assert stats.getTotLinesRead() == 8;
        assert stats.getNumOfAnalyzedFiles() == 6;
        assert stats.getNumOfAnalyzedDirs() == 3;
    }
}
