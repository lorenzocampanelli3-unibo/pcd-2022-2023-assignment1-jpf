package pcd.assignment1.jpf.tasks;

import pcd.assignment1.jpf.model.MockFile;
import pcd.assignment1.jpf.model.MockStats;
import pcd.assignment1.jpf.pool.CustomThreadPool;

public class MockLinesCountTask implements Task {

    private MockFile file;
    private CustomThreadPool pool;
    private MockStats mockStats;

    public MockLinesCountTask(MockFile file, CustomThreadPool pool, MockStats mockStats) {
        this.file = file;
        this.pool = pool;
        this.mockStats = mockStats;
    }


    @Override
    public void run() {
        if (!pool.isShutDown()) {
            this.mockStats.updateFileStats(this.file.getNumLines());
        }
    }

    @Override
    public String toString() {
        return "MockLinesCountTask{" +
                "file=" + file.getAbsolutePath() +
                '}';
    }
}
