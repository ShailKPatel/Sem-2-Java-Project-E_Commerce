package com.shailkpatel.cravings.util;

public class LinkedList<T> {
    Node head;
    int size = 0;

    public class Node {
        public T data;
        public Node next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public void addLast(T data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    public void addFirst(T data) {
        Node newNode = new Node(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    public boolean remove(T data) {
        if (head == null)
            return false;

        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.data.equals(data)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean removeLast() {
        if (head == null)
            return false;

        if (head.next == null) {
            head = null;
            size--;
            return true;
        }

        Node current = head;
        while (current.next.next != null) {
            current = current.next;
        }
        current.next = null;
        size--;
        return true;
    }

    public boolean removeFirst() {
        if (head == null)
            return false;
        head = head.next;
        size--;
        return true;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public int size() {
        return size;
    }

    public void display() {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    public Node getHead() {
        return head;
    }

    public Node getHeadNode() {
        if (head == null) {
            throw new IllegalStateException("List is empty.");
        }
        return head;
    }

    public boolean isEmpty() {
        return head == null;
    }
}