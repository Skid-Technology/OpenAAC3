package me.konsolas.aac.punishment;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class DescendingValueMapSorter {
    public static <K, V> Map<K, V> sortDescending(Map<K, V> map) {
        LinkedList<Map.Entry<K, V>> linkedList = new LinkedList<>(map.entrySet());
        Collections.sort(linkedList, new DescendingEntryComparator());
        LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : linkedList) {
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
    }
}

