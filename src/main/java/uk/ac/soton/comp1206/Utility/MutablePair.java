package uk.ac.soton.comp1206.Utility;

import javafx.util.Pair;

/**
 * The pair in javafx doesn't allow you to change
 *  the value of a key once it has been set
 * 
 * In our case we will store scores that will change
 *  throughout runtime so we need to be able to change it
 */
public class MutablePair<K, V> {
    //Unique key to represent a value
    private final  K key;

    //Value of the pair
    private V value;
    
    /**
     * Creates a new mutable pair
     * @param key The key for this pair
     * @param value The pair's value
     */
    public MutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Changes the value of the key
     * @param value The new value
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Checks if two pairs are equal
     * @param o Object to compare to it
     */
    public boolean equals(MutablePair<K, V> o) {
        if (this == o) return true;
        
        if (this.key != null && !this.key.equals(o.getKey())) return false;
        if (this.value != null && !this.value.equals(o.getValue())) return false;
        
        return true;
    }

    /**
     * Changes pair to ordinary immutable pair
     * @return the javafx pair version of the pair
     */
    public Pair<K, V> toPair() {
        return new Pair<K, V>(key, value);
    }

    /**
     * print output of the pair
     */
    public String toString() {
        return this.key + ":" + this.value;
    }

    /**
     * @return The pair's key
     */
    public K getKey() {
        return this.key;
    }

    /**
     * @return The pair's value
     */
    public V getValue() {
        return this.value;
    }
}
