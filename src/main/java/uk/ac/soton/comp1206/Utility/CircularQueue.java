package uk.ac.soton.comp1206.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic circular queue
 */
public class CircularQueue<T> {
    private ArrayList<T> elements = new ArrayList<>();
    private int pointer = 0;

    /**
     * Constructor for adding existing elements
     * @param elements
     */
    public CircularQueue(List<T> elements) {
        this.elements.addAll(elements);
    }

    //Constructor for no parameters
    public CircularQueue() {}

    /**
     * Returns the next element and increments the pointer
     * @return
     */
    public T dequeue() {
        var element = this.elements.get(pointer);
        pointer = (pointer+1)%this.elements.size();

        return element;
    }

    /**
     * Adds a value to the queue
     * @param element
     */
    public void enqueue(T element) {
        this.elements.add(element);
    }

    /**
     * Returns the next element without incrementing the pointer
     * @return
     */
    public T peek() {
        return this.elements.get(pointer);
    }

    public int size() {
        return this.elements.size();
    }

}
