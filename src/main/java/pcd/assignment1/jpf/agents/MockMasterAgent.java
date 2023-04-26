package pcd.assignment1.jpf.agents;

import pcd.assignment1.jpf.model.MockDirectory;
import pcd.assignment1.jpf.tasks.MockDirectoryListingTask;
import pcd.assignment1.jpf.model.MockStats;
import pcd.assignment1.jpf.pool.CustomDynamicThreadPoolImpl;
import pcd.assignment1.jpf.pool.CustomThreadPool;
import pcd.assignment1.jpf.util.Flag;
import pcd.assignment1.jpf.util.Log;

public class MockMasterAgent extends Thread {
//    private int maxWorkersPoolSize;

    private MockDirectory rootDir;

    private MockStats mockStats;
    private Flag stopFlag;
    private CustomThreadPool pool;


    public MockMasterAgent(int maxWorkersPoolSize, MockDirectory rootDir, MockStats mockStats, Flag stopFlag) {
        super("Master-Agent");
        this.mockStats = mockStats;
        this.rootDir = rootDir;
        this.stopFlag = stopFlag;
        this.pool = new CustomDynamicThreadPoolImpl(maxWorkersPoolSize, stopFlag);
    }

    @Override
    public void run() {
        if (!stopFlag.isSet()) {
            Log.log("Master agent started.");
            this.pool.execute(new MockDirectoryListingTask(rootDir, pool, mockStats));
            this.pool.joinAllWorkers();
        }
        /*Stats.Snapshot snapshot = this.stats.getSnapshot();
        List<Pair<String, Long>> filesLinesCountSnap = snapshot.getFilesLinesCountSnap();
        Map<Range, Long> distributionSnap = snapshot.getDistributionSnap();

        // print the lines count table
        ConsoleTable linesCountTable = new ConsoleTable().withStyle(Styles.BASIC);
        linesCountTable.setHeaders("File Path", "# Lines");
        filesLinesCountSnap.forEach(p -> linesCountTable.addRow(p.getX(), p.getY()));
        System.out.println("LINES COUNT:");
        System.out.print(linesCountTable);

        // print the distribution table
        ConsoleTable distributionTable = new ConsoleTable().withStyle(Styles.BASIC);
        distributionTable.setHeaders("Range", "# Files");
        distributionSnap.forEach(distributionTable::addRow);
        System.out.println("DISTRIBUTION:");
        System.out.print(distributionTable);

        Log.log("Analysis complete. Analyzed " + this.stats.getNumOfAnalyzedFiles() +
                " files in " + this.stats.getNumOfAnalyzedDirs() + " directories.");*/
    }
}
