package org.tbank.collections;

import lombok.Getter;
import java.util.List;
import java.util.stream.Stream;

/*
 TODO:
 1. add(1);
    get(0);
    remove(0);
    contains(1);
    addAll(List);
 2. Stream <T> -> CustomLinkedList<T> (use reduce())
*/

public class CustomLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;
    @Getter
    private int size;


    private static class Node<T> {
        T element;
        Node<T> previous;
        Node<T> next;

        public Node(T element) {
            this.element = element;
            this.previous = null;
            this.next = null;
        }
    }

    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }


    public void add(T element) {
        Node<T> elementNode = new Node<T>(element);

        if (isEmpty()) {
            head = elementNode;

        } else {
            tail.next = elementNode;
            elementNode.previous = tail;
        }

        tail = elementNode;
        size++;
    }


    public T get(int index) {
        return getNode(index).element;
    }


    private Node<T> getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> result = head;

        for (int i = 0; i < index; i++) {
            result = result.next;
        }

        return result;
    }


    public void remove(int index) {
        Node<T> current = getNode(index); // тут будет проверка на IndexOutOfBoundsException()

        if (index == 0) {
            head = head.next;
            if (head != null) {
                head.previous = null;
            } else {
                tail = null;
            }

        } else if (index == size - 1) {
            tail = tail.previous;
            tail.next = null;

        } else {
            current.previous.next = current.next;
            current.next.previous = current.previous;
        }

        size--;
    }


    public boolean contains(T element) {
        Node<T> current = head;

        while (current != null) {
            if (current.element.equals(element)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }


    public void addAll(List<T> list) {
        list.forEach(this::add);
    }


    public void addAll(CustomLinkedList<T> list) {
        if (!list.isEmpty()) {

            if (isEmpty()) {
                head = list.head;

            } else {
                tail.next = list.head;
                list.head.previous = tail;
            }
            tail = list.tail;
            size += list.size;
        }
    }


    public boolean isEmpty() {
        return head == null;
    }


    public static <T> CustomLinkedList<T> collectFromStream(Stream<T> stream) {

        return stream.reduce(
                new CustomLinkedList<>(),
                (list, element) -> {
                    list.add(element);
                    return list;
                },
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                }
        );
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        var current = head;
        while (current != null) {
            sb.append(current.element);
            if (current.next != null) {
                sb.append(" -> ");
            }
            current = current.next;
        }

        sb.append("]");
        return sb.toString();
    }
}
