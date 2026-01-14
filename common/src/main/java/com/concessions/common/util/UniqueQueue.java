package com.concessions.common.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class UniqueQueue<T> implements Iterable<T> {

    private final Queue<T> queue = new LinkedList<>();
    private final Set<T> set = new HashSet<>();

    /**
     * Adds an element to the end of the queue if it is not already present.
     *
     * @param element The element to add.
     * @return {@code true} if the element was added, {@code false} otherwise.
     */
    public boolean add(T element) {
        if (set.add(element)) {
            queue.add(element);
            return true;
        }
        return false;
    }

    /**
     * Retrieves and removes the head of this queue.
     *
     * @return The head of this queue, or {@code null} if this queue is empty.
     */
    public T poll() {
        T element = queue.poll();
        if (element != null) {
            set.remove(element);
        }
        return element;
    }

    /**
     * Retrieves, but does not remove, the head of this queue.
     *
     * @return The head of this queue, or {@code null} if this queue is empty.
     */
    public T peek() {
        return queue.peek();
    }

    /**
     * Returns the number of elements in this queue.
     *
     * @return The number of elements in this queue.
     */
    public int size() {
        return queue.size();
    }

    /**
     * Returns {@code true} if this queue contains the specified element.
     *
     * @param element The element whose presence in this queue is to be tested.
     * @return {@code true} if this queue contains the specified element.
     */
    public boolean contains(T element) {
        return set.contains(element);
    }

    /**
     * Returns {@code true} if this queue contains no elements.
     *
     * @return {@code true} if this queue contains no elements.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns an iterator over the elements in this queue.
     *
     * @return An iterator over the elements in this queue.
     */
    @Override
    public Iterator<T> iterator() {
        return queue.iterator();
    }

    @Override
    public String toString() {
        return queue.toString();
    }
}
