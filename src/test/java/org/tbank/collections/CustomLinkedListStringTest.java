package org.tbank.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CustomLinkedListStringTest {

    private CustomLinkedList<String> stringList;

    @BeforeEach
    void setUp() {
        stringList = new CustomLinkedList<>();
        stringList.add("A"); // [A]
    }

    @Test
    void addTest() {
        assertEquals(1, stringList.getSize());
        stringList.add("B");
        stringList.add("C");
        assertEquals(3, stringList.getSize());
    }

    @Test
    void getTest() {
        assertEquals("A", stringList.get(0));
        stringList.add("B");
        assertEquals("B", stringList.get(1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            stringList.get(2);
        });
    }

    @Test
    void removeTest() {
        stringList.remove(0);
        assertTrue(stringList.isEmpty());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            stringList.remove(0);
        });
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            stringList.remove(1);
        });
    }

    @Test
    void containsTest() {
        assertTrue(stringList.contains("A"));
        assertFalse(stringList.contains("D"));
    }

    @Test
    void addAllTest() {
        List<String> list = List.of("C", "D");
        stringList.addAll(list);

        assertTrue(stringList.contains("A"));
        assertTrue(stringList.contains("D"));
    }

    @Test
    void isEmptyTest() {
        assertFalse(stringList.isEmpty());
        stringList.remove(0);
        assertTrue(stringList.isEmpty());
    }

    @Test
    void toStringTest() {
        List<String> list = List.of("B", "C");
        stringList.addAll(list);
        assertEquals("[A -> B -> C]", stringList.toString());
    }

    @Test
    void stringTest() {
        CustomLinkedList<String> stringCLL = new CustomLinkedList<>();
        stringCLL.add("Milk");

        assertTrue(stringCLL.contains("Milk"));
        assertEquals("Milk", stringCLL.get(0));

        stringCLL.add("Bread");

        assertEquals(2, stringCLL.getSize());
        assertEquals("[Milk -> Bread]", stringCLL.toString());

        stringCLL.remove(0);

        assertEquals("Bread", stringCLL.get(0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            stringCLL.remove(1);
        });

        List<String> list = List.of("Cheese", "Coffee");
        stringCLL.addAll(list);

        assertTrue(stringCLL.contains("Cheese"));
        assertFalse(stringCLL.contains("Tea"));
        assertEquals("[Bread -> Cheese -> Coffee]", stringCLL.toString());
    }

    @Test
    void collectFromStreamTest() {
        Stream<String> strStream = Stream.of("A", "B", "C", "D");
        CustomLinkedList<String> stringCustomLinkedList = CustomLinkedList
                .collectFromStream(strStream);

        assertEquals(4, stringCustomLinkedList.getSize());
        assertEquals("A", stringCustomLinkedList.get(0));
        assertTrue(stringCustomLinkedList.contains("C"));
    }
}
