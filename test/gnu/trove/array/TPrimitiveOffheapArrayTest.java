package gnu.trove.array;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TPrimitiveOffheapArrayTest extends TestCase {

    private TIntOffheapArray list;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        list = new TIntOffheapArray(5);
        assertEquals(5, list.capacity());
        list.put(0, 1);
        list.put(1, 2);
        list.put(2, 3);
        list.put(3, 4);
        list.put(4, 5);
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGet() {
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, list.get(i));
        }

        try {
            list.get(list.capacity());
            fail("Expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException ex) {
            // Expected
        }
    }

    public void testPut() {
        list.put(0, 5);
        list.put(1, 4);
        list.put(2, 3);
        list.put(3, 2);
        list.put(4, 1);

        for (int i = 0; i < 5; i++) {
            assertEquals(5 - i, list.get(i));
        }

        try {
            list.put(5, 20);
            fail("Expected IndexOutOfBoundsException");
        }
        catch ( IndexOutOfBoundsException ex ) {
            // Expected
        }
    }

    public void testResize() {
        list.resize(10);
        testGet();
        list.put(9, 100);
        assertEquals(0, list.get(8));
        assertEquals(100, list.get(9));

        list.resize(3);
        assertEquals(3, list.capacity());
        for (int i = 0; i < 3; i++) {
            assertEquals(i + 1, list.get(i));
        }
    }

    public void testResizeStress() {
        list.clear();
        for (int i = 1; i < 1000000; i++) {
            list.resize(5 * i);
            list.put(5 * i - 1, i);
        }
        for (int i = 1; i < 1000000; i++) {
            assertEquals(0, list.get(5 * i - 2));
            assertEquals(i, list.get(5 * i - 1));
        }
    }

    public void testClear() {
        list.clear();
        assertEquals(5, list.capacity());

        for (int i = 0; i < 5; i++) {
            assertEquals(0, list.get(i));
        }
    }

    public void testFree() {
        list.free();
        try {
            list.put(0, 20);
            fail("Expected IndexOutOfBoundsException");
        }
        catch ( IndexOutOfBoundsException ex ) {
            // Expected
        }
    }

    public void testFreeStress() {
        List<TIntOffheapArray> list = new ArrayList<TIntOffheapArray>();
        for (int i = 0; i < 10000; i++) {
            list.add(new TIntOffheapArray(1000000 + 100 * i));
            list.get(i).free();
        }
    }

//    public void testGcStress() {
//        for (int i = 0; i < 100; i++) {
//            new TIntOffheapArray(50000000);
//        }
//    }

    public void testToArray() {
        int[] array = new int[10];
        list.toArray(2, array, 1, 3);
        assertEquals(0, array[0]);
        assertEquals(3, array[1]);
        assertEquals(4, array[2]);
        assertEquals(5, array[3]);
        assertEquals(0, array[4]);

        list.resize(100);
        for (int i = 0; i < 100; i++) {
            list.put(i, i);
        }
        array = new int[100];
        list.toArray(30, array, 20, 60);
        for (int i = 0; i < 20; i++) {
            assertEquals(0, array[i]);
        }
        for (int i = 20; i < 80; i++) {
            assertEquals(i + 10, array[i]);
        }
        for (int i = 80; i < 100; i++) {
            assertEquals(0, array[i]);
        }
    }
}
