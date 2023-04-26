package pcd.assignment1.jpf.model;

public class MockFile implements MockEntry {
    private String absolutePath;
    private long nLines;

    private MockEntryType type;

    public MockFile(String absolutePath, long nLines) {
        this.type = MockEntryType.FILE;
        this.absolutePath = absolutePath;
        this.nLines = nLines;
    }

    @Override
    public String getAbsolutePath() {
        return this.absolutePath;
    }

    public long getNumLines() {
        return nLines;
    }

    @Override
    public MockEntryType getType() {
        return type;
    }
}
