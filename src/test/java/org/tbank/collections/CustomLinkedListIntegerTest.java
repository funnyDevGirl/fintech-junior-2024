package org.tbank.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class CustomLinkedListIntegerTest {

    private CustomLinkedList<Integer> integerList;

    @BeforeEach
    void setUp() {
        integerList = new CustomLinkedList<>();
        integerList.add(1); // [1]
    }

    @Test
    void addTest() {
        assertEquals(1, integerList.getSize());
        integerList.add(2);
        integerList.add(3);
        assertEquals(3, integerList.getSize());
    }

    @Test
    void getTest() {
        assertEquals(1, (int) integerList.get(0));
        integerList.add(2);
        assertEquals(2, (int) integerList.get(1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            integerList.get(2);
        });
    }

    @Test
    void removeTest() {
        integerList.remove(0);
        assertTrue(integerList.isEmpty());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            integerList.remove(0);
        });
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            integerList.remove(1);
        });
    }

    @Test
    void containsTest() {
        assertTrue(integerList.contains(1));
        assertFalse(integerList.contains(10));
    }

    @Test
    void addAllTest() {
        List<Integer> list = List.of(3, 4);
        integerList.addAll(list);

        assertTrue(integerList.contains(1));
        assertTrue(integerList.contains(4));
    }

    @Test
    void isEmptyTest() {
        assertFalse(integerList.isEmpty());
        integerList.remove(0);
        assertTrue(integerList.isEmpty());
    }

    @Test
    void toStringTest() {
        List<Integer> list = List.of(3, 4);
        integerList.addAll(list);
        assertEquals("[1 -> 3 -> 4]", integerList.toString());
    }

    @Test
    void collectFromStreamTest() {
        Stream<Integer> intStream = Stream.of(1, 2, 3, 4, 5);
        CustomLinkedList<Integer> integerCustomLinkedList = CustomLinkedList
                .collectFromStream(intStream);

        assertEquals(5, integerCustomLinkedList.getSize());
        assertEquals(1, (int) integerCustomLinkedList.get(0));
        assertTrue(integerCustomLinkedList.contains(2));
    }
}
