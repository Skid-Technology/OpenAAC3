package me.konsolas.aac.punishment;

import java.util.Comparator;
import java.util.Map;

final class DescendingEntryComparator
implements Comparator {
    public DescendingEntryComparator() {
    }

    public int compareEntries(Map.Entry entry, Map.Entry entry2) {
        return ((Comparable)entry2.getValue()).compareTo(entry.getValue());
    }

    public int compare(Object obj, Object obj2) {
        return this.compareEntries((Map.Entry)obj, (Map.Entry)obj2);
    }
}

