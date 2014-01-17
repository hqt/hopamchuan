package com.hac.android.helper.collections;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom Collection
 * Use this collection with (Key-Value) Pair like Map
 * But limited to number of elements in collection
 * when collection is full, oldest element will be replaced
 * @author Huynh Quang Thao
 *
 * @param <K> : type of key
 * @param <E> : type of element
 */
public class LimitedPipeArray<K extends Comparable<K>,E> {

    private int n;
    private int  rear = 0;
    // list all elements of collection. just limited to pre-define size
    private Object[] elements;
    // map from key to index of element should be seen
    private Map<K, Integer> keys;
    // use this to store location of key of element at index i
    private Object[] locRefKeys;

    public LimitedPipeArray(int n) {
        this.n = n;
        elements = new Object[n];
        locRefKeys = new Object[n];
        keys = new HashMap<K, Integer>();
    }

    /**
     * Insert into Collection element E with Key K
     * if Key already exist, new Element will be override
     * Else. new Element will be inserted into Collection
     * And if Collection is full, oldest element will be replaced
     * @param key
     * @param element
     */
    public void insert(K key, E element) {
        if (keys.containsKey(key)) {
            // if contains key. just replace old element
            int index = keys.get(key);
            elements[index] = element;
        } else {
            // delete oldest element in collection by remove from key
            K oldKey = (K) locRefKeys[rear];
            keys.remove(oldKey);
            // create new key and value
            keys.put(key, rear);
            locRefKeys[rear] = key;
            elements[rear] = element;
            rear = (rear + 1) % n;
        }
    }

    public E get(K key) {
        Integer index = keys.get(key);
        if (index == null) return null;
        else return (E)elements[index];
    }

    public int getSize() { return n;}

    /**
     * clear all currently data
     */
    public void clear() {
        rear = 0;
        elements = new Object[n];
        locRefKeys = new Object[n];
        keys.clear();
    }

    public static void main(String[] args) {
        LimitedPipeArray<Integer, People> list = new LimitedPipeArray<Integer, People>(3);
        list.insert(1, new People("thao"));
        list.insert(2, new People("hieu"));
        list.insert(3, new People("du"));
        System.out.println(list.get(1));	// should be thao
        System.out.println(list.get(2));	// should be hieu
        System.out.println(list.get(3));	// should be du


        list.insert(4, new People("hoa"));
        System.out.println(list.get(1));	// should be null
        System.out.println(list.get(4));	// should be hoa
        list.insert(4, new People("thu hoa"));
        System.out.println(list.get(4));	// should be thu hoa

        list.insert(5, new People("kim du"));
        list.insert(6, new People("kim du"));
        list.insert(7, new People("tran kim du"));
        System.out.println(list.get(4));	// should be null
        System.out.println(list.get(7));	// should be tran kim du


    }

    private static class People {
        public String name;
        public People(String name) {
            this.name = name;
        }
        @Override
        public String toString() {
            return name;
        }
    }
}
