package gnu.trove.list.array;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import gnu.trove.function.TIntFunction;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import junit.framework.TestCase;



public class TPrimitiveOffheapArrayListTest extends TestCase {

    private TIntList list;


    @Override
    public void setUp() throws Exception {
        super.setUp();

        list = new TIntOffheapArrayList();
        list.add( 1 );
        list.add( 2 );
        list.add( 3 );
        list.add( 4 );
        list.add( 5 );
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testGet() {

        assertEquals( 4, list.get( 3 ) );

        try {
            list.get( 10 );
            fail( "Expected IndexOutOfBoundsException" );
        }
        catch ( IndexOutOfBoundsException ex ) {
            // Expected
        }

        int element_count = 10;
        TIntOffheapArrayList array_list = new TIntOffheapArrayList();
        for ( int i = 1; i <= element_count; i++ ) {
            array_list.add( i );
        }

        for ( int i = 0; i < array_list.size(); i++ ) {
            int expected = i + 1;
            assertEquals( expected, array_list.getQuick( i ) );
        }

        try {
            array_list.getQuick( 100 );
            fail( "Expected IndexOutOfBoundsException" );
        }
        catch ( IndexOutOfBoundsException ex ) {
            // Expected
        }

    }


    public void testSetArray() {
        int element_count = 10;
        int[] ints = {1138, 42, 86, 99, 101};

        TIntList a = new TIntOffheapArrayList();
        assertTrue( a.isEmpty() );

        for ( int i = 1; i <= element_count; i++ ) {
            a.add( i );
        }

        a.set( a.size() - ints.length, ints );

        for ( int i = 0; i < element_count - ints.length; i++ ) {
            assertEquals( i + 1, a.get( i ) );
        }
        for ( int i = element_count - ints.length, j = 0;
              i < a.size();
              i++, j++ ) {
            assertEquals( ints[j], a.get( i ) );
        }

        try {
            a.set( a.size(), ints );
            fail( "Expected IndexOutOfBoundsException" );
        }
        catch ( IndexOutOfBoundsException ex ) {
            // Expected
        }
    }


    public void testSet() {
        int element_count = 10;

        TIntList a = new TIntOffheapArrayList();
        assertTrue( a.isEmpty() );

        for ( int i = 1; i <= element_count; i++ ) {
            a.add( i );
        }

        int testval = 1138;
        a.set( 5, testval );

        for ( int i = 0; i < 5; i++ ) {
            int result = a.get( i );
            int expected = i + 1;
            assertTrue( "element " + result + " should be " + expected,
                    result == expected );
        }

        assertEquals( testval, a.get( 5 ) );

        for ( int i = 6; i < a.size(); i++ ) {
            int result = a.get( i );
            int expected = i + 1;
            assertTrue( "element " + result + " should be " + expected,
                    result == expected );
        }

        try {
            a.set( 100, 20 );
            fail( "Expected IndexOutOfBoundsException" );
        }
        catch ( IndexOutOfBoundsException ex ) {
            // Expected
        }
    }


    public void testSetQuick() {
        int element_count = 10;

        TIntOffheapArrayList a = new TIntOffheapArrayList();
        assertTrue( a.isEmpty() );

        for ( int i = 1; i <= element_count; i++ ) {
            a.add( i );
        }

        int testval = 1138;
        a.setQuick( 5, testval );

        for ( int i = 0; i < 5; i++ ) {
            int result = a.get( i );
            int expected = i + 1;
            assertTrue( "element " + result + " should be " + expected,
                    result == expected );
        }

        assertEquals( testval, a.get( 5 ) );

        for ( int i = 6; i < a.size(); i++ ) {
            int result = a.get( i );
            int expected = i + 1;
            assertTrue( "element " + result + " should be " + expected,
                    result == expected );
        }

        try {
            a.setQuick( 100, 20 );
            fail( "Expected IndexOutOfBoundsException" );
        }
        catch ( IndexOutOfBoundsException ex ) {
            // Expected
        }
    }


    public void testReplace() {
        int element_count = 10;

        TIntList a = new TIntOffheapArrayList();
        assertTrue( a.isEmpty() );

        for ( int i = 1; i <= element_count; i++ ) {
            a.add( i );
        }

        int testval = 1138;
        assertEquals( 6, a.replace( 5, testval ) );

        for ( int i = 0; i < 5; i++ ) {
            int result = a.get( i );
            int expected = i + 1;
            assertTrue( "element " + result + " should be " + expected,
                    result == expected );
        }

        assertEquals( testval, a.get( 5 ) );

        for ( int i = 6; i < a.size(); i++ ) {
            int result = a.get( i );
            int expected = i + 1;
            assertTrue( "element " + result + " should be " + expected,
                    result == expected );
        }

        try {
            a.replace( 100, 20 );
            fail( "Expected IndexOutOfBoundsException" );
        }
        catch ( IndexOutOfBoundsException ex ) {
            // Expected
        }
    }


    public void testAddAllCollection() {
        int element_count = 20;
        SortedSet<Integer> set = new TreeSet<Integer>();
        for ( int i = 0; i < element_count; i++ ) {
            set.add( Integer.valueOf( i ) );
        }

        TIntOffheapArrayList list = new TIntOffheapArrayList( 20 );
        for ( int i = 0; i < element_count; i++ ) {
            list.add( i );
        }

        assertEquals( element_count, set.size() );
        assertEquals( element_count, list.size() );

        list.addAll( set );
        assertEquals( element_count * 2, list.size() );
        for ( int i = 0; i < list.size(); i++ ) {
            int expected;
            if ( i < element_count ) {
                expected = i;
            } else {
                expected = i - element_count;
            }
            assertEquals( expected , list.get( i ) );
        }
    }


    public void testAddAllTCollection() {
        int element_count = 20;
        TIntOffheapArrayList source = new TIntOffheapArrayList();
        for ( int i = 0; i < element_count; i++ ) {
            source.add( Integer.valueOf( i ) );
        }

        TIntOffheapArrayList list = new TIntOffheapArrayList( 20 );
        for ( int i = 0; i < element_count; i++ ) {
            list.add( i );
        }

        assertEquals( element_count, source.size() );
        assertEquals( element_count, list.size() );

        list.addAll( source );
        assertEquals( element_count * 2, list.size() );
        for ( int i = 0; i < list.size(); i++ ) {
            int expected;
            if ( i < element_count ) {
                expected = i;
            } else {
                expected = i - element_count;
            }
            assertEquals( expected , list.get( i ) );
        }
    }


    public void testAddAllArray() {
        int element_count = 20;
        int[] ints = new int[element_count];
        TIntOffheapArrayList list = new TIntOffheapArrayList();
        for ( int i = 0; i < element_count; i++ ) {
            ints[i] = i;
            list.add( Integer.valueOf( i ) );
        }

        assertEquals( element_count, list.size() );

        assertTrue ( list.addAll( ints ) );
        assertEquals( element_count * 2, list.size() );
        for ( int i = 0; i < list.size(); i++ ) {
            int expected;
            if ( i < element_count ) {
                expected = i;
            } else {
                expected = i - element_count;
            }
            assertEquals( "expected: " + expected + ", got: " + list.get( i ) +
                    ", list: " + list + ", array: " + Arrays.toString( ints ),
                    expected , list.get( i ) );
        }
    }


    public void testAddArray() {
        TIntOffheapArrayList list = new TIntOffheapArrayList();
        list.add(1);
        list.add(new int[] {2, 3, 4, 5, 6, 7, 8});
        list.add(9);
        list.add(new int[] {10, 11, 12, 13});
        list.add(14);
        assertEquals(14, list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(i + 1, list.get(i));
        }
    }


    public void testIterator() {
        int element_count = 20;
        TIntList list = new TIntOffheapArrayList();
        for ( int i = 0; i < element_count; i++ ) {
            list.add( i );
        }

        TIntIterator iter = list.iterator();
        assertTrue( "iter should have next: " + list.size(), iter.hasNext() );

        int j = 0;
        while ( iter.hasNext() ) {
            int next = iter.next();
            assertEquals( j, next );
            j++;
        }
        assertFalse( iter.hasNext() );
    }


    public void testIteratorAbuseNext() {
        int element_count = 20;
        TIntList list = new TIntOffheapArrayList();
        for ( int i = 0; i < element_count; i++ ) {
            list.add( i );
        }

        TIntIterator iter = list.iterator();
        while ( iter.hasNext() ) {
            iter.next();
        }
        assertFalse( iter.hasNext() );
    }


    public void testEnsureCapacity() {
        int size = 1000;
        TIntOffheapArrayList array_list = new TIntOffheapArrayList();
        int initial_length = array_list.capacity();
        assertEquals( Constants.DEFAULT_CAPACITY, initial_length );

        array_list.ensureCapacity( size );
        int max_length = array_list.capacity();
        assertTrue( "not large enough: " + max_length + " should be >= " + size,
                max_length >= size );
    }


    public void testTrimToSize() {
        int initial_size = 1000;
        int element_count = 100;

        TIntOffheapArrayList array_list = new TIntOffheapArrayList( initial_size );
        int initial_length = array_list.capacity();
        assertEquals( initial_size, initial_length );
        assertTrue( array_list.isEmpty() );

        for ( int i = 1; i <= element_count; i++ ) {
            array_list.add( i );
        }
        array_list.trimToSize();

        int trimmed_length = array_list.capacity();
        assertTrue( "not trimmed: " + trimmed_length + " should be == " + element_count,
                trimmed_length == element_count );
    }


    public void testToArray() {
        assertTrue( Arrays.equals( new int[]{1, 2, 3, 4, 5}, list.toArray() ) );
        assertTrue( Arrays.equals( new int[]{1, 2, 3, 4}, list.toArray( 0, 4 ) ) );
        assertTrue( Arrays.equals( new int[]{2, 3, 4, 5}, list.toArray( 1, 4 ) ) );
        assertTrue( Arrays.equals( new int[]{2, 3, 4}, list.toArray( 1, 3 ) ) );

        try {
            list.toArray( -1, 5 );
            fail( "Expected ArrayIndexOutOfBoundsException when begin < 0" );
        }
        catch ( ArrayIndexOutOfBoundsException expected ) {
            // Expected
        }
    }


    public void testToArrayWithDest() {
        int[] dest = new int[5];
        assertTrue( Arrays.equals( new int[]{1, 2, 3, 4, 5}, list.toArray( dest ) ) );
        dest = new int[4];
        assertTrue( Arrays.equals( new int[]{1, 2, 3, 4}, list.toArray( dest, 0, 4 ) ) );
        dest = new int[4];
        assertTrue( Arrays.equals( new int[]{2, 3, 4, 5}, list.toArray( dest, 1, 4 ) ) );
        dest = new int[3];
        assertTrue( Arrays.equals( new int[]{2, 3, 4}, list.toArray( dest, 1, 3 ) ) );

        try {
            list.toArray( dest, -1, 5 );
            fail( "Expected ArrayIndexOutOfBoundsException when begin < 0" );
        }
        catch ( ArrayIndexOutOfBoundsException expected ) {
            // Expected
        }
    }


    public void testToArrayWithDestTarget() {
        int[] dest = new int[5];
        assertTrue( Arrays.equals( new int[]{1, 2, 3, 4, 5}, list.toArray( dest ) ) );
        dest = new int[4];
        assertTrue( Arrays.equals( new int[]{1, 2, 3, 4}, list.toArray( dest, 0, 0, 4 ) ) );
        dest = new int[5];
        assertTrue( Arrays.equals( new int[]{0, 2, 3, 4, 5}, list.toArray( dest, 1, 1, 4 ) ) );
        dest = new int[4];
        assertTrue( Arrays.equals( new int[]{0, 2, 3, 4}, list.toArray( dest, 1, 1, 3 ) ) );

        dest = new int[5];
        assertTrue( Arrays.equals( new int[]{0, 0, 0, 0, 0}, list.toArray( dest, 0, 0, 0 ) ) );

        try {
            list.toArray( dest, -1, 0, 5 );
            fail( "Expected ArrayIndexOutOfBoundsException when begin < 0" );
        }
        catch ( ArrayIndexOutOfBoundsException expected ) {
            // Expected
        }
    }


    public void testSubList() throws Exception {
        TIntList subList = list.subList( 1, 4 );
        assertEquals( 3, subList.size() );
        assertEquals( 2, subList.get( 0 ) );
        assertEquals( 4, subList.get( 2 ) );
    }


    public void testSublist_Exceptions() {
        try {
            list.subList( 1, 0 );
            fail( "expected IllegalArgumentException when end < begin" );
        }
        catch ( IllegalArgumentException expected ) {
        }

        try {
            list.subList( -1, 3 );
            fail( "expected IndexOutOfBoundsException when begin < 0" );
        }
        catch ( IndexOutOfBoundsException expected ) {
        }

        try {
            list.subList( 1, 42 );
            fail( "expected IndexOutOfBoundsException when end > length" );
        }
        catch ( IndexOutOfBoundsException expected ) {
        }
    }


    public void testIndexOf() {
        int element_count = 20;
        TIntList a = new TIntOffheapArrayList();
        for ( int i = 1; i <= element_count; i++ ) {
            a.add( i );
        }

        int index = a.indexOf( 10 );
        assertEquals( 9, index );

        // Add more elements, but duplicates
        for ( int i = 1; i <= element_count; i++ ) {
            a.add( i );
        }
        index = a.indexOf( 10 );
        assertEquals( 9, index );

        index = a.indexOf( 5 );
        assertEquals( 4, index );

        index = a.lastIndexOf( 5 );
        assertEquals( 24, index );

        // Non-existant entry
        index = a.indexOf( 100 );
        assertEquals( -1, index );
    }


    public void testReset() {
        int element_count = 20;
        TIntOffheapArrayList a = new TIntOffheapArrayList( 20, Integer.MIN_VALUE );
        for ( int i = 1; i <= element_count; i++ ) {
            a.add( i );
        }

        assertEquals( element_count, a.size() );
        a.reset();
        assertEquals( 0, a.size() );
    }


    public void testGrep() {
        int element_count = 20;
        TIntList a = new TIntOffheapArrayList();
        for ( int i = 1; i < element_count; i++ ) {
            a.add( i );
        }

        TIntList grepped = a.grep( new TIntProcedure() {
            @Override
            public boolean execute( int value ) {
                return value > 10;
            }
        } );

        for ( int i = 0; i < grepped.size(); i++ ) {
            int expected = i + 11;
            assertEquals( expected, grepped.get( i ) );
        }
    }


    public void testInverseGrep() {
        int element_count = 20;
        TIntList a = new TIntOffheapArrayList();
        for ( int i = 1; i < element_count; i++ ) {
            a.add( i );
        }

        TIntList grepped = a.inverseGrep( new TIntProcedure() {
            @Override
            public boolean execute( int value ) {
                return value <= 10;
            }
        } );

        for ( int i = 0; i < grepped.size(); i++ ) {
            int expected = i + 11;
            assertEquals( expected, grepped.get( i ) );
        }
    }


    public void testMax() {
        assertEquals( 5, list.max() );
        assertEquals( 1, list.min() );

        TIntList list2 = new TIntOffheapArrayList();
        assertTrue( list2.isEmpty() );
        list2.add( 3 );
        list2.add( 1 );
        list2.add( 2 );
        list2.add( 5 );
        list2.add( 4 );
        assertEquals( 5, list2.max() );
        assertEquals( 1, list2.min() );

        try {
            TIntList list3 = new TIntOffheapArrayList();
            list3.min();
            fail( "Expected IllegalStateException" );
        }
        catch ( IllegalStateException ex ) {
            // Expected
        }

        try {
            TIntList list3 = new TIntOffheapArrayList();
            list3.max();
            fail( "Expected IllegalStateException" );
        }
        catch ( IllegalStateException ex ) {
            // Expected
        }
    }


    public void testForEach() {
        int element_count = 20;
        TIntList a = new TIntOffheapArrayList();
        for ( int i = 1; i < element_count; i++ ) {
            a.add( i );
        }

        class ForEach implements TIntProcedure {
            TIntList built = new TIntOffheapArrayList();


            @Override
            public boolean execute( int value ) {
                built.add( value );
                return true;
            }

            TIntList getBuilt() {
                return built;
            }
        }

        ForEach foreach = new ForEach();
        a.forEach( foreach );
        TIntList built = foreach.getBuilt();
        assertEquals( a, built );
    }


    public void testForEachFalse() {
        int element_count = 20;
        TIntList a = new TIntOffheapArrayList();
        for ( int i = 1; i < element_count; i++ ) {
            a.add( i );
        }

        class ForEach implements TIntProcedure {
            TIntList built = new TIntOffheapArrayList();


            @Override
            public boolean execute( int value ) {
                built.add( value );
                return false;
            }

            TIntList getBuilt() {
                return built;
            }
        }

        ForEach foreach = new ForEach();
        a.forEach( foreach );
        TIntList built = foreach.getBuilt();
        assertEquals( 1, built.size() );
        assertEquals( 1, built.get( 0 ) );
    }


    public void testForEachDescending() {
        int element_count = 20;
        TIntList a = new TIntOffheapArrayList();
        for ( int i = 1; i < element_count; i++ ) {
            a.add( i );
        }

        class ForEach implements TIntProcedure {
            TIntList built = new TIntOffheapArrayList();


            @Override
            public boolean execute( int value ) {
                built.add( value );
                return true;
            }
        }

        ForEach foreach = new ForEach();
        a.forEachDescending( foreach );
    }


    public void testForEachDescendingFalse() {
        int element_count = 20;
        TIntList a = new TIntOffheapArrayList();
        for ( int i = 1; i < element_count; i++ ) {
            a.add( i );
        }

        class ForEach implements TIntProcedure {
            TIntList built = new TIntOffheapArrayList();


            @Override
            public boolean execute( int value ) {
                built.add( value );
                return false;
            }

            TIntList getBuilt() {
                return built;
            }
        }

        ForEach foreach = new ForEach();
        a.forEachDescending( foreach );
        TIntList built = foreach.getBuilt();
        assertEquals( 1, built.size() );
        assertEquals( 19, built.get( 0 ) );
    }


    public void testTransform() {
        int element_count = 20;
        TIntList a = new TIntOffheapArrayList();
        for ( int i = 1; i < element_count; i++ ) {
            a.add( i );
        }

        a.transformValues( new TIntFunction() {
            @Override
            public int execute( int value ) {
                return value * value;
            }
        } );

        for ( int i = 0; i < a.size(); i++ ) {
            int result = a.get( i );
            int expected = ( i + 1 ) * ( i + 1 );
            assertEquals( expected, result );
        }
    }


    public void testSerialization() throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream( bout );
        oout.writeObject( list );
        oout.close();

        ObjectInputStream oin = new ObjectInputStream(
                new ByteArrayInputStream( bout.toByteArray() ) );

        TIntOffheapArrayList new_list = (TIntOffheapArrayList) oin.readObject();

        assertEquals( list, new_list );
    }
}
