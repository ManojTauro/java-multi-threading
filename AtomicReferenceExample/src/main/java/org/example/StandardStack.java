package org.example;

public class StandardStack<T> {
    private StackNode<T> head;
    private int counter = 0;

    public synchronized void push(T value) {
        StackNode<T> newNode = new StackNode<>(value);
        newNode.setNext(head);
        head = newNode;
        counter++;
    }

    public synchronized T pop() {
        if (head == null) {
            counter++;
            return null;
        }

        T value = head.getValue();
        head = head.getNext();
        counter++;

        return value;
    }

    public int getCounter() {
        return counter;
    }
}
