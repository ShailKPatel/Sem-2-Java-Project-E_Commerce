package com.shailkpatel.cravings.util;

public class Stack<T> {
    public LinkedList<T> list = new LinkedList<>();

    public void push(T element) {
        list.addFirst(element);
    }

    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty.");
        }
        T topElement = list.get(list.size() - 1);
        list.removeLast();
        return topElement;
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty.");
        }
        return list.getHead().data;
    }

    public boolean isEmpty() {
        return list.getHead() == null;
    }

    public int size() {
        return list.size();
    }

    public void viewStack() {
        list.display();
    }
}
