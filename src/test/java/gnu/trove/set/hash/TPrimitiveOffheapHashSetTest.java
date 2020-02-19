package gnu.trove.set.hash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import junit.framework.TestCase;


/**
 * Test the primitive HashSet classes.
 */
public class TPrimitiveOffheapHashSetTest extends TestCase {

    public TPrimitiveOffheapHashSetTest(String name) {
        super(name);
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testIsEmpty() throws Exception {
        TIntSet s = new TIntOffheapHashSet();
        assertTrue("new set wasn't empty", s.isEmpty());

        s.add(1);
        assertTrue("set with element reports empty", !s.isEmpty());
        s.clear();
        assertTrue("cleared set reports not-empty", s.isEmpty());
    }


    public void testContains() throws Exception {
        TIntSet s = new TIntOffheapHashSet();
        int i = 100;
        s.add(i);
        assertTrue("contains failed", s.contains(i));
        assertFalse("contains failed", s.contains(1000));
    }


    public void testContainsAll() throws Exception {

        int[] ints = {1138, 42, 13, 86, 99};

        TIntSet set = new TIntOffheapHashSet();
        set.addAll(ints);

        TIntSet other = new TIntOffheapHashSet();
        other.addAll(ints);

        List<Number> ints_list = new ArrayList<Number>();
        for (int element : ints) {
            ints_list.add(element);
        }

        for (int index = 0; index < ints.length; index++) {
            assertTrue(Integer.valueOf(ints[index]).toString(),
                    set.contains(ints[index]));
        }

        assertTrue("containsAll(Collection<?>) failed: " + set,
                set.containsAll(ints_list));
        ints_list.remove(Integer.valueOf(42));
        ints_list.add(Long.valueOf(42));
        assertFalse("containsAll(Collection<?>) failed: " + set,
                set.containsAll(ints_list));

        assertTrue("containsAll(TIntSet) failed (same set): " + set,
                set.containsAll(set));

        assertTrue("containsAll(TIntSet) failed (other set): " + set,
                set.containsAll(other));

        assertTrue("containsAll(int[]) failed: " + set,
                set.containsAll(ints));


        int[] failed = {42, 86, 99, 123456};

        TIntSet failed_set = new TIntOffheapHashSet();
        failed_set.addAll(failed);

        List<Integer> failed_list = new ArrayList<Integer>();
        for (int element : failed) {
            failed_list.add(element);
        }

        assertFalse("containsAll(Collection<?>) failed (false positive): " + set,
                set.containsAll(failed_list));

        assertFalse("containsAll(TIntSet) failed (false positive): " + set,
                set.containsAll(failed_set));

        assertFalse("containsAll(int[]) failed (false positive): " + set,
                set.containsAll(failed));
    }


    public void testAddAll() throws Exception {

        int[] ints = {1138, 42, 13, 86, 99, 101};

        TIntSet set;

        List<Integer> list = new ArrayList<Integer>();
        for (int element : ints) {
            list.add(Integer.valueOf(element));
        }

        set = new TIntOffheapHashSet();
        assertTrue("addAll(Collection<?>) failed: " + set, set.addAll(list));
        for (int element : ints) {
            assertTrue("contains failed: ", set.contains(element));
        }

        set = new TIntOffheapHashSet();
        assertTrue("addAll(int[]) failed: " + set, set.addAll(ints));
        for (int element : ints) {
            assertTrue("contains failed: ", set.contains(element));
        }

        TIntSet test_set = new TIntOffheapHashSet();
        assertTrue("addAll(TIntSet) failed: " + test_set, test_set.addAll(set));
        for (int element : ints) {
            assertTrue("contains failed: ", set.contains(element));
        }


    }


    public void testRetainAll() throws Exception {

        int[] ints = {1138, 42, 13, 86, 99, 101};

        TIntSet set = new TIntOffheapHashSet();
        set.addAll(ints);

        TIntSet other = new TIntOffheapHashSet();
        other.addAll(ints);

        int[] to_retain = {13, 86, 99, 1138};

        TIntSet retain_set = new TIntOffheapHashSet();
        retain_set.addAll(to_retain);

        List<Integer> retain_list = new ArrayList<Integer>();
        for (int element : to_retain) {
            retain_list.add(element);
        }

        assertFalse("retainAll(TIntSet) failed (same set): " + set,
                set.retainAll(set));
        // Contains all the original elements
        assertTrue(set.toString(), set.containsAll(ints));
        assertTrue(retain_set.toString(), retain_set.containsAll(to_retain));

        assertTrue("retainAll(Collection<?>) failed: " + set,
                set.retainAll(retain_list));
        // Contains just the expected elements
        assertFalse(set.toString(), set.containsAll(ints));
        assertTrue(set.toString(), set.containsAll(to_retain));
        assertTrue(retain_set.toString(), retain_set.containsAll(to_retain));

        // reset the set.
        set = new TIntOffheapHashSet();
        set.addAll(ints);
        assertTrue("retainAll(TIntSet) failed: " + set,
                set.retainAll(retain_set));
        // Contains just the expected elements
        assertFalse(set.toString(), set.containsAll(ints));
        assertTrue(set.toString(), set.containsAll(to_retain));
        assertTrue(retain_set.toString(), retain_set.containsAll(to_retain));

        // reset the set.
        set = new TIntOffheapHashSet();
        set.addAll(ints);
        assertTrue("retainAll(int[]) failed: " + set,
                set.retainAll(to_retain));
        // Contains just the expected elements
        assertFalse(set.toString(), set.containsAll(ints));
        assertTrue(set.toString(), set.containsAll(to_retain));
        assertTrue(retain_set.toString(), retain_set.containsAll(to_retain));
    }


    public void testRemoveAll() throws Exception {

        int[] ints = {1138, 42, 13, 86, 99, 101};

        TIntSet set = new TIntOffheapHashSet();
        set.addAll(ints);

        TIntSet other = new TIntOffheapHashSet();
        other.addAll(ints);

        int[] to_remove = {13, 86, 99, 1138};

        TIntSet remove_set = new TIntOffheapHashSet();
        remove_set.addAll(to_remove);

        List<Integer> remove_list = new ArrayList<Integer>();
        for (int element : to_remove) {
            remove_list.add(element);
        }

        int[] remainder = {42, 101};

        try {
            assertFalse("removeAll(TIntSet) failed (same set): " + set,
                    set.removeAll(set));
            fail("should have thrown ConcurrentModificationException");
        } catch (ConcurrentModificationException cme) {
            // expected exception thrown.
        }

        // reset the set.
        set = new TIntOffheapHashSet();
        set.addAll(ints);
        assertTrue("removeAll(Collection<?>) failed: " + set,
                set.removeAll(remove_list));
        // Contains just the expected elements
        assertTrue(set.toString(), set.containsAll(remainder));
        assertFalse(set.toString(), set.containsAll(to_remove));
        assertTrue(remove_set.toString(), remove_set.containsAll(to_remove));

        // reset the set.
        set = new TIntOffheapHashSet();
        set.addAll(ints);
        assertTrue("removeAll(TIntSet) failed: " + set,
                set.removeAll(remove_set));
        // Contains just the expected elements
        assertTrue(set.toString(), set.containsAll(remainder));
        assertFalse(set.toString(), set.containsAll(to_remove));
        assertTrue(remove_set.toString(), remove_set.containsAll(to_remove));

        // reset the set.
        set = new TIntOffheapHashSet();
        set.addAll(ints);
        assertTrue("removeAll(int[]) failed: " + set,
                set.removeAll(to_remove));
        // Contains just the expected elements
        assertTrue(set.toString(), set.containsAll(remainder));
        assertFalse(set.toString(), set.containsAll(to_remove));
        assertTrue(remove_set.toString(), remove_set.containsAll(to_remove));
    }


    public void testAdd() throws Exception {
        TIntSet set = new TIntOffheapHashSet();
        assertTrue("add failed", set.add(1));
        assertFalse("duplicated add modified set", set.add(1));
    }


    public void testRemove() throws Exception {
        TIntSet set = new TIntOffheapHashSet();
        set.add(1);
        set.add(2);
        assertTrue("One was not added", set.contains(1));
        assertTrue("One was not removed", set.remove(1));
        assertFalse("One was not removed", set.contains(1));
        assertTrue("Two was also removed", set.contains(2));
    }


    public void testRemoveNonExistant() throws Exception {
        TIntSet set = new TIntOffheapHashSet();
        set.add(1);
        set.add(2);
        assertTrue("One was not added", set.contains(1));
        assertTrue("One was not removed", set.remove(1));
        assertFalse("One was not removed", set.contains(1));
        assertTrue("Two was also removed", set.contains(2));
        assertFalse("Three was removed (non-existant)", set.remove(3));
    }


    public void testSize() throws Exception {
        TIntSet set = new TIntOffheapHashSet();
        assertEquals("initial size was not 0", 0, set.size());

        for (int i = 0; i < 99; i++) {
            set.add(i);
            assertEquals("size did not increase after add", i + 1, set.size());
        }
    }


    public void testClear() throws Exception {
        TIntSet set = new TIntOffheapHashSet();
        set.addAll(new int[]{1, 2, 3});
        assertEquals("size was not 3", 3, set.size());
        set.clear();
        assertEquals("initial size was not 0", 0, set.size());
    }


    public void testSerialize() throws Exception {
        int[] ints = {1138, 42, 86, 99, 101};

        TIntSet set = new TIntOffheapHashSet();
        set.addAll(ints);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(set);

        ByteArrayInputStream bias = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bias);

        TIntSet deserialized = (TIntSet) ois.readObject();

        assertEquals(set, deserialized);
    }


    public void testToArray() {
        TIntSet set = new TIntOffheapHashSet();
        int[] ints = {42, 1138, 13, 86, 99};
        set.addAll(ints);
        int[] res = set.toArray();
        Arrays.sort(ints);
        Arrays.sort(res);
        assertTrue(Arrays.equals(ints, res));
    }


    public void testToArrayMatchesIteratorOrder() {
        TIntSet set = new TIntOffheapHashSet();
        int[] ints = {42, 1138, 13, 86, 99};
        set.addAll(ints);
        int[] toarray_ints = set.toArray();

        int[] iter_ints = new int[5];
        TIntIterator iter = set.iterator();

        int index = 0;
        while (iter.hasNext()) {
            iter_ints[index++] = iter.next();
        }

        assertTrue(Arrays.equals(iter_ints, toarray_ints));
    }


    public void testRehashing() throws Exception {
        int size = 10000;
        TIntSet set = new TIntOffheapHashSet(10);
        for (int i = 0; i < size; i++) {
            set.add(i);
        }
        assertEquals(set.size(), size);
    }


    public void testIterator() {

        TIntSet set = new TIntOffheapHashSet();
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);

        TIntIterator iter = set.iterator();
        assertTrue("iterator should have a next item", iter.hasNext());

        int last = -1;
        while (iter.hasNext()) {
            int next = iter.next();
            assertTrue(Integer.valueOf(next).toString(),
                    next >= 1 && next <= 4);
            assertTrue(Integer.valueOf(next).toString(), next != last);
            last = next;
        }

        assertFalse("iterator should not have a next item", iter.hasNext());

        assertTrue("set should contain 1", set.contains(1));
        assertTrue("set should contain 2", set.contains(2));
        assertTrue("set should contain 3", set.contains(3));
        assertTrue("set should contain 4", set.contains(4));
        assertEquals(4, set.size());
    }


    public void testIteratorRemove() {

        TIntSet set = new TIntOffheapHashSet();
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);

        TIntIterator iter = set.iterator();
        assertTrue("iterator should have a next item", iter.hasNext());

        int last = -1;
        while (iter.hasNext()) {
            int next = iter.next();
            assertTrue(next >= 1 && next <= 4);
            assertTrue(next != last);
            last = next;

            if (next == 3) {
                iter.remove();
            }
        }

        assertFalse("iterator should not have a next item", iter.hasNext());

        assertFalse("set should not contain 3", set.contains(3));
        assertTrue("set should contain 1", set.contains(1));
        assertTrue("set should contain 2", set.contains(2));
        assertTrue("set should contain 4", set.contains(4));
        assertEquals(3, set.size());

    }


    public void testForEach() throws IOException {
        TIntSet set = new TIntOffheapHashSet(10, 0.5f);
        int[] ints = {1138, 42, 86, 99, 101};
        set.addAll(ints);

        class ForEach implements TIntProcedure {

            TIntSet built = new TIntOffheapHashSet();


            @Override
            public boolean execute(int value) {
                built.add(value);
                return true;
            }


            TIntSet getBuilt() {
                return built;
            }
        }

        ForEach procedure = new ForEach();

        set.forEach(procedure);
        TIntSet built = procedure.getBuilt();

        assertEquals("inequal sizes: " + set + ", " + built, set.size(), built.size());
        assertTrue("inequal sets: " + set + ", " + built, set.equals(built));
    }


    public void testEquals() {
        int[] ints = {1138, 42, 86, 99, 101};
        TIntSet set = new TIntOffheapHashSet();
        set.addAll(ints);
        TIntSet other = new TIntOffheapHashSet();
        other.addAll(ints);

        assertTrue("sets incorrectly not equal: " + set + ", " + other,
                set.equals(other));

        int[] mismatched = {72, 49, 53, 1024, 999};
        TIntSet unequal = new TIntOffheapHashSet();
        unequal.addAll(mismatched);

        assertFalse("sets incorrectly equal: " + set + ", " + unequal,
                set.equals(unequal));

        // Change length, different code branch
        unequal.add(1);
        assertFalse("sets incorrectly equal: " + set + ", " + unequal,
                set.equals(unequal));
    }


    public void testHashcode() {
        int[] ints = {1138, 42, 86, 99, 101};
        TIntSet set = new TIntOffheapHashSet();
        set.addAll(ints);
        TIntSet other = new TIntOffheapHashSet();
        other.addAll(ints);

        assertTrue("hashcodes incorrectly not equal: " + set + ", " + other,
                set.hashCode() == other.hashCode());

        int[] mismatched = {72, 49, 53, 1024, 999};
        TIntSet unequal = new TIntOffheapHashSet();
        unequal.addAll(mismatched);

        assertFalse("hashcodes unlikely equal: " + set + ", " + unequal,
                set.hashCode() == unequal.hashCode());
    }

    public void test3445639() throws Exception {
        // Retain all bug AIOBE
        TIntOffheapHashSet hs = new TIntOffheapHashSet(23, 1f);
        hs.addAll(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22});
        hs.retainAll(new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22});
        hs.retainAll(new int[]{18});
    }
}
