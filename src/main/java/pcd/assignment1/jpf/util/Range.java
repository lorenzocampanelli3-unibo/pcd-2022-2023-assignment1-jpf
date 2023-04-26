package pcd.assignment1.jpf.util;

import java.util.Objects;

public class Range implements Comparable<Range> {
    private long min;
    private long max;

    public Range(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public boolean contains(long value) {
        return (value >= min && value <= max);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return min == range.min && max == range.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Override
    public int compareTo(Range o) {
        int minComparison = Long.compare(this.getMin(), o.getMin());
        return minComparison == 0 ? Long.compare(this.getMax(), o.getMax()) : minComparison;
    }

    @Override
    public String toString() {
        return "[" + min + " , " + max + "]";
    }
}
