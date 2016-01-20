package ru.finnetrolle.separator;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

/**
 * Created by finnetrolle on 21.01.2016.
 */
public class SeparatorTest {

    public static class SomeException extends Exception {
        private final String code;

        public SomeException(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    private final static String MESSAGE = "I am some exception";
    private final static int COUNT = 100;

    @Test
    public void testForkJoin() throws Exception {
        List<Integer> ilist = IntStream.range(0, COUNT).boxed().collect(toList());
        List<String> slist = Separator.forkJoin(ilist, x -> x.stream().map(Object::toString).collect(Collectors.toList()), 3);
        assertEquals(COUNT, slist.size());
        for (int i = 0; i < COUNT; ++i) {
            assertEquals(String.valueOf(ilist.get(i)), slist.get(i));
        }
    }

    @Test(expected = SomeException.class)
    public void testForkJoinException() throws Exception {
        List<Integer> ilist = IntStream.rangeClosed(0, COUNT).boxed().collect(toList());
        List<String> slist = Separator.forkJoin(ilist, x -> iWillThrow(x), 3);
    }

    @Test
    public void testCatchingException() {
        List<Integer> ilist = IntStream.rangeClosed(0, COUNT).boxed().collect(toList());
        try {
            Separator.forkJoin(ilist, x -> iWillThrow(x), 3);
        } catch (SomeException e) {
            assertEquals(MESSAGE, e.getCode());
            return;
        }
        assertEquals(true, false); // We never should be here
    }

    private List<String> iWillThrow(List<Integer> list) throws SomeException {
        throw new SomeException(MESSAGE);
    }

}