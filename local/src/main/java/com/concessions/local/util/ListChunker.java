package com.concessions.local.util;

import java.util.ArrayList;
import java.util.List;

public class ListChunker {
    /**
     * Splits a list into sublists of a maximum size.
     */
    public static <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            int endIndex = Math.min(i + chunkSize, list.size());
            chunks.add(list.subList(i, endIndex));
        }
        return chunks;
    }
}