package com.hqt.hac.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class LazyLoadingList<T> {

    List<T> list;

    private LazyLoadingList() {
        list = new ArrayList<T>();
    }

    public LazyLoadingList(int totalElement) {
        this();
        list.addAll(Collections.<T>nCopies(totalElement, null));
    }

    public LazyLoadingList(int totalElement, int capacity) {
        list = new ArrayList<T>(capacity);
        list.addAll(Collections.<T>nCopies(totalElement, null));
    }

    public LazyLoadingList(ArrayList<T> collection) {
        this();
        list.addAll(collection);
    }

    public T get(int index) {
        if (index < list.size()) {
            // within bound. get object
            // and see it null or not (because lazy loading property)
            // if null. load object. not. return
            T obj = list.get(index);
            if (obj == null) {
                T newObj = loadData(index);
                list.set(index, newObj);
                return newObj;
            }
            // else. already load in list
            return obj;
        }

        // if not. we should grow the list
        // use this method for performance
        list.addAll(Collections.<T>nCopies(index - list.size() + 1, null));

        // create last object. set and return
        T newObj = loadData(index);
        list.set(index, newObj);
        return newObj;
    }

    /**
     * set element at index
     * if index OutOfBound. will automatically throw OutOfBound Index Exception
     */
    public void set(int index, T element) {
        list.set(index, element);
    }

    private T getHack(int index) { return list.get(index);}

    /**
     * Client uses this class must implement this method to decide how to load data at index ith
     * @param index index should to be load
     * @return return data type
     */
    public abstract T loadData(int index);

    public static void main(String[] args) {
        LazyLoadingList<People> list = new LazyLoadingList<LazyLoadingList.People>(5) {
            @Override
            public LazyLoadingList.People loadData(int index) {
                return new People("thao " + index);
            }
        };

        System.out.printf("index %d: %s\n", 0, list.get(0));	// should be thao 0
        System.out.printf("index %d: %s\n", 5, list.get(5));	// should be thao 5
        System.out.printf("index %d: %s\n", 3, list.getHack(3));	// should be null
        System.out.printf("index %d: %s\n", 6, list.get(6));	// should be null
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
