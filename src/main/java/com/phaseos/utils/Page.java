package com.phaseos.utils;

import java.util.List;

public class Page {

    private Object next;
    private Object prev;
    private Object items;
    private int pageSize;
    private int pageNum;

    public Page(Object next, Object prev, int pageNum, int pageSize) {
        this.next = next;
        this.prev = prev;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Object getNext() {
        return next;
    }

    public void setNext(Object next) {
        this.next = next;
    }

    public Object getPrev() {
        return prev;
    }

    public void setPrev(Object prev) {
        this.prev = prev;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void addItem(Object item) {
        try {
            ((List<Object>) (items)).add(item);
        } catch (ClassCastException e) {
            System.out.println("The member field \'items\' was cast as a List<Item>, but could not be cast as such.\n\'items\' is of type: " + items.getClass().getCanonicalName());
        }
    }

    public void setItemAsString(StringBuilder lines) {
        items = lines;
    }

}
