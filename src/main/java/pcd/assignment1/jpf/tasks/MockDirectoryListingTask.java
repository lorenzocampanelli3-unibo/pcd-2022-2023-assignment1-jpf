package pcd.assignment1.jpf.tasks;

import gov.nasa.jpf.vm.Verify;
import pcd.assignment1.jpf.model.*;
import pcd.assignment1.jpf.pool.CustomThreadPool;
import pcd.assignment1.jpf.util.Log;

import java.util.Iterator;

public class MockDirectoryListingTask implements Task {

    private MockDirectory directory;
    private CustomThreadPool pool;

    private MockStats mockStats;

    public MockDirectoryListingTask(MockDirectory directory, CustomThreadPool pool, MockStats mockStats) {
        this.directory = directory;
        this.pool = pool;
        this.mockStats = mockStats;
    }

    @Override
    public void run() {
//        Log.log("Analysis of dir " + this.directory.getAbsolutePath() + " started.");
        boolean analysisStarted = false;
        Iterator<MockEntry> iterator = this.directory.getEntries().stream().iterator();
        while (!pool.isShutDown() && iterator.hasNext()) {
            Verify.beginAtomic();
            if (!analysisStarted) {
                analysisStarted = true;
            }
            MockEntry entry = iterator.next();
//            Log.log("Found entry: " + entry.getAbsolutePath());
            Verify.endAtomic();
            if (entry.getType() == MockEntryType.DIRECTORY) {
                pool.execute(new MockDirectoryListingTask((MockDirectory) entry, pool, mockStats));
            } else {
                pool.execute(new MockLinesCountTask((MockFile) entry, pool, mockStats));
            }
        }
        if (analysisStarted) {
            this.mockStats.updateDirStats();
        }
    }

    @Override
    public String toString() {
        return "MockDirectoryListingTask{" +
                "directory=" + directory.getAbsolutePath() +
                '}';
    }
}
