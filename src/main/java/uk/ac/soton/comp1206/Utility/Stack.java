package uk.ac.soton.comp1206.Utility;

import java.util.ArrayList;

/**
 * Basic stack class
 * @author tcs1g20
 */
public class Stack<T> {
    //List of elements
    private ArrayList<T> stack = new ArrayList<>();

    //Points to the next location
    private int pointer = 0;

    /**
     * Push an object onto the stack
     * @param obj Object being pushed
     */
    public void push(T obj) {
        this.stack.add(obj);
        this.pointer++;
    }

    /**
     * Remove and return an element from the stack
     * @return the top element of the stack
     */
    public T pop() {
        if (this.stack.size() > 0) {
            var obj = this.stack.get(pointer-1);
            this.stack.remove(pointer-1);
            pointer--;
            return obj;
        }

        return null;
    }

    /**
     * Returns the top element of the stack
     * @return The top element
     */
    public T peek() {
        if (this.pointer > 0) return this.stack.get(pointer-1);
        else return null;
    }

    /**
     * @return The size of the stack
     */
    public int size() {
        return this.pointer;
    }

    /**
     * Replaces the top element of the stack
     * If the stack is empty it just pushes the object
     * @param obj The replacement object
     */
    public void overwrite(T obj) {
        if (pointer > 0) this.stack.set(pointer, obj);
        else this.push(obj);
    }
}
