package pcd.assignment1.jpf.model;

import java.util.LinkedList;
import java.util.List;

public class MockDirectory implements MockEntry {
    private String absolutePath;
    private MockEntryType type;

    private List<MockEntry> entries;

    public MockDirectory(String absolutePath) {
        this.absolutePath = absolutePath;
        this.type = MockEntryType.DIRECTORY;
        this.entries = new LinkedList<>();
    }
    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public MockEntryType getType() {
        return type;
    }

    public void addEntry(MockEntry entry) {
        this.entries.add(entry);
    }

    public List<MockEntry> getEntries() {
        return this.entries;
    }


}
