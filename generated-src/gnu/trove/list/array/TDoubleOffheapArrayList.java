///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
// Copyright (c) 2009, Rob Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package gnu.trove.list.array;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Random;

import gnu.trove.array.*;

import gnu.trove.impl.HashFunctions;

import gnu.trove.*;
import gnu.trove.function.*;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.*;
import gnu.trove.list.*;
import gnu.trove.procedure.*;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////


/**
 * A resizable, array-backed list of double primitives.
 */
public class TDoubleOffheapArrayList implements TDoubleList, Externalizable {
    static final long serialVersionUID = 1L;

    /** the data of the list */
    protected TDoubleOffheapArray _data;

    /** the index after the last entry in the list */
    protected int _pos;

    /** the default capacity for new lists */
    protected static final int DEFAULT_CAPACITY = Constants.DEFAULT_CAPACITY;

    /** the double value that represents null */
    protected double no_entry_value;

    /**
     * Creates a new <code>TDoubleOffheapArrayList</code> instance with the
     * default capacity.
     */
    public TDoubleOffheapArrayList() {
        this( DEFAULT_CAPACITY, ( double ) 0 );
    }


    /**
     * Creates a new <code>TDoubleOffheapArrayList</code> instance with the
     * default capacity.
     */
    public TDoubleOffheapArrayList( int capacity ) {
        this( capacity, ( double ) 0 );
    }


    /**
     * Creates a new <code>TDoubleOffheapArrayList</code> instance with the
     * specified capacity.
     *
     * @param capacity an <code>int</code> value
     * @param no_entry_value an <code>double</code> value that represents null.
     */
    public TDoubleOffheapArrayList( int capacity, double no_entry_value ) {
        _data = new TDoubleOffheapArray( capacity );
        _pos = 0;
        this.no_entry_value = no_entry_value;
    }


    /** {@inheritDoc} */
    @Override
    public double getNoEntryValue() {
        return no_entry_value;
    }


    // sizing

    /**
     * Grow the internal array as needed to accommodate the specified number of elements.
     * The size of the array bytes on each resize unless capacity requires more than twice
     * the current capacity.
     */
    public void ensureCapacity( int capacity ) {
        int oldCapacity = capacity();
        if ( capacity > oldCapacity ) {
            int newCap = Math.max( oldCapacity << 1, capacity );
            _data.resize( newCap );
        }
    }


    /** {@inheritDoc} */
    @Override
    public int size() {
        return _pos;
    }


    protected int capacity() {
        return (int)_data.capacity();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return _pos == 0;
    }


    /**
     * Sheds any excess capacity above and beyond the current size of the list.
     */
    public void trimToSize() {
        if ( capacity() > size() ) {
            _data.resize( size() );
        }
    }


    // modifying

    /** {@inheritDoc} */
    @Override
    public boolean add( double val ) {
        ensureCapacity( _pos + 1 );
        _data.put( _pos++, val );
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void add( double[] vals ) {
        add( vals, 0, vals.length );
    }


    /** {@inheritDoc} */
    @Override
    public void add( double[] vals, int offset, int length ) {
        ensureCapacity( _pos + length );
        _data.fromArray( vals, offset, _pos, length );
        _pos += length;
    }


    /** {@inheritDoc} */
    @Override
    public void insert( int offset, double value ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public void insert( int offset, double[] values ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public void insert( int offset, double[] values, int valOffset, int len ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public double get( int offset ) {
        if ( offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        return _data.get( offset );
    }


    /**
     * Returns the value at the specified offset without doing any bounds checking.
     */
    public double getQuick( int offset ) {
        return _data.get( offset );
    }


    /** {@inheritDoc} */
    @Override
    public double set( int offset, double val ) {
        if ( offset >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }

        double prev_val = _data.get( offset );
        _data.put( offset, val );
        return prev_val;
    }


    /** {@inheritDoc} */
    @Override
    public double replace( int offset, double val ) {
        return set( offset, val );
    }


    /** {@inheritDoc} */
    @Override
    public void set( int offset, double[] values ) {
        set( offset, values, 0, values.length );
    }


    /** {@inheritDoc} */
    @Override
    public void set( int offset, double[] values, int valOffset, int length ) {
        if ( offset < 0 || offset + length > _pos ) {
            throw new ArrayIndexOutOfBoundsException( offset );
        }
        _data.fromArray( values, valOffset, offset, length );
    }


    /**
     * Sets the value at the specified offset without doing any bounds checking.
     */
    public void setQuick( int offset, double val ) {
        _data.put( offset, val );
    }


    /** {@inheritDoc} */
    @Override
    public void clear() {
        clear( DEFAULT_CAPACITY );
    }


    /**
     * Flushes the internal state of the list, setting the capacity of the empty list to
     * <tt>capacity</tt>.
     */
    public void clear( int capacity ) {
        _data.resize( capacity );
        _data.clear();
        _pos = 0;
    }


    /**
     * Sets the size of the list to 0, but does not change its capacity. This method can
     * be used as an alternative to the {@link #clear()} method if you want to recycle a
     * list without allocating new backing arrays.
     */
    public void reset() {
        _pos = 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean remove( double value ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public double removeAt( int offset ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public void remove( int offset, int length ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public TDoubleIterator iterator() {
        return new TDoubleOffheapArrayIterator( 0 );
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsAll( Collection<?> collection ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsAll( TDoubleCollection collection ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsAll( double[] array ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public boolean addAll( Collection<? extends Double> collection ) {
        boolean changed = false;
        for ( Double element : collection ) {
            double e = element.doubleValue();
            if ( add( e ) ) {
                changed = true;
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    @Override
    public boolean addAll( TDoubleCollection collection ) {
        boolean changed = false;
        TDoubleIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            double element = iter.next();
            if ( add( element ) ) {
                changed = true;
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    @Override
    public boolean addAll( double[] array ) {
        boolean changed = false;
        for ( double element : array ) {
            if ( add( element ) ) {
                changed = true;
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    @Override
    public boolean retainAll( Collection<?> collection ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public boolean retainAll( TDoubleCollection collection ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public boolean retainAll( double[] array ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public boolean removeAll( Collection<?> collection ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public boolean removeAll( TDoubleCollection collection ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public boolean removeAll( double[] array ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public void transformValues( TDoubleFunction function ) {
        for ( int i = _pos; i-- > 0; ) {
            _data.put( i, function.execute( _data.get( i ) ) );
        }
    }


    /** {@inheritDoc} */
    @Override
    public void reverse() {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public void reverse( int from, int to ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public void shuffle( Random rand ) {
        throw new UnsupportedOperationException();
    }


    // copying

    /** {@inheritDoc} */
    @Override
    public TDoubleList subList( int begin, int end ) {
        if ( end < begin ) {
            throw new IllegalArgumentException( "end index " + end +
                " greater than begin index " + begin );
        }
        if ( begin < 0 ) {
            throw new IndexOutOfBoundsException( "begin index can not be < 0" );
        }
        if ( end > capacity() ) {
            throw new IndexOutOfBoundsException( "end index < " + capacity() );
        }
        TDoubleArrayList list = new TDoubleArrayList( end - begin );
        _data.toArray(begin, list._data, 0, end - begin);
        list._pos = end - begin;
        return list;
    }


    /** {@inheritDoc} */
    @Override
    public double[] toArray() {
        return toArray( 0, _pos );
    }


    /** {@inheritDoc} */
    @Override
    public double[] toArray( int offset, int len ) {
        double[] rv = new double[ len ];
        toArray( rv, offset, len );
        return rv;
    }


    /** {@inheritDoc} */
    @Override
    public double[] toArray( double[] dest ) {
        int len = dest.length;
        if ( dest.length > _pos ) {
            len = _pos;
            dest[len] = no_entry_value;
        }
        toArray( dest, 0, len );
        return dest;
    }


    /** {@inheritDoc} */
    @Override
    public double[] toArray( double[] dest, int offset, int len ) {
        return toArray( dest, offset, 0, len );
    }


    /** {@inheritDoc} */
    @Override
    public double[] toArray( double[] dest, int source_pos, int dest_pos, int len ) {
        if ( len == 0 ) {
            return dest;             // nothing to copy
        }
        if ( source_pos < 0 || source_pos >= _pos ) {
            throw new ArrayIndexOutOfBoundsException( source_pos );
        }
        _data.toArray( source_pos, dest, dest_pos, len );
        return dest;
    }


    // comparing

    /** {@inheritDoc} */
    @Override
    public boolean equals( Object other ) {
        if ( other == this ) {
            return true;
        }
        else if ( other instanceof TDoubleOffheapArrayList ) {
            TDoubleOffheapArrayList that = ( TDoubleOffheapArrayList )other;
            if ( that.size() != this.size() ) return false;
            else {
                for ( int i = _pos; i-- > 0; ) {
                    if ( this._data.get( i ) != that._data.get( i ) ) {
                        return false;
                    }
                }
                return true;
            }
        }
        else return false;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int h = 0;
        for ( int i = _pos; i-- > 0; ) {
            h += HashFunctions.hash( _data.get( i ) );
        }
        return h;
    }


    // procedures

    /** {@inheritDoc} */
    @Override
    public boolean forEach( TDoubleProcedure procedure ) {
        for ( int i = 0; i < _pos; i++ ) {
            if ( !procedure.execute( _data.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachDescending( TDoubleProcedure procedure ) {
        for ( int i = _pos; i-- > 0; ) {
            if ( !procedure.execute( _data.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }


    // sorting

    /** {@inheritDoc} */
    @Override
    public void sort() {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public void sort( int fromIndex, int toIndex ) {
        throw new UnsupportedOperationException();
    }


    // filling

    /** {@inheritDoc} */
    @Override
    public void fill( double val ) {
        throw new UnsupportedOperationException();
    }


    /** {@inheritDoc} */
    @Override
    public void fill( int fromIndex, int toIndex, double val ) {
        throw new UnsupportedOperationException();
    }


    // searching

    /** {@inheritDoc} */
    @Override
    public int binarySearch( double value ) {
        return binarySearch( value, 0, _pos );
    }


    /** {@inheritDoc} */
    @Override
    public int binarySearch(double value, int fromIndex, int toIndex) {
        if ( fromIndex < 0 ) {
            throw new ArrayIndexOutOfBoundsException( fromIndex );
        }
        if ( toIndex > _pos ) {
            throw new ArrayIndexOutOfBoundsException( toIndex );
        }

        int low = fromIndex;
        int high = toIndex - 1;

        while ( low <= high ) {
            int mid = ( low + high ) >>> 1;
            double midVal = _data.get( mid );

            if ( midVal < value ) {
                low = mid + 1;
            }
            else if ( midVal > value ) {
                high = mid - 1;
            }
            else {
                return mid; // value found
            }
        }
        return -( low + 1 );  // value not found.
    }


    /** {@inheritDoc} */
    @Override
    public int indexOf( double value ) {
        return indexOf( 0, value );
    }


    /** {@inheritDoc} */
    @Override
    public int indexOf( int offset, double value ) {
        for ( int i = offset; i < _pos; i++ ) {
            if ( _data.get( i ) == value ) {
                return i;
            }
        }
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int lastIndexOf( double value ) {
        return lastIndexOf( _pos, value );
    }


    /** {@inheritDoc} */
    @Override
    public int lastIndexOf( int offset, double value ) {
        for ( int i = offset; i-- > 0; ) {
            if ( _data.get( i ) == value ) {
                return i;
            }
        }
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean contains( double value ) {
        return lastIndexOf( value ) >= 0;
    }


    /** {@inheritDoc} */
    @Override
    public TDoubleList grep( TDoubleProcedure condition ) {
        TDoubleArrayList list = new TDoubleArrayList();
        for ( int i = 0; i < _pos; i++ ) {
            double val = _data.get( i );
            if ( condition.execute( val ) ) {
                list.add( val );
            }
        }
        return list;
    }


    /** {@inheritDoc} */
    @Override
    public TDoubleList inverseGrep( TDoubleProcedure condition ) {
        TDoubleArrayList list = new TDoubleArrayList();
        for ( int i = 0; i < _pos; i++ ) {
            double val = _data.get( i );
            if ( !condition.execute( val ) ) {
                list.add( val );
            }
        }
        return list;
    }


    /** {@inheritDoc} */
    @Override
    public double max() {
        if ( size() == 0 ) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        }
        double max = Double.NEGATIVE_INFINITY;
        for ( int i = 0; i < _pos; i++ ) {
            double val = _data.get( i );
            if ( val > max ) {
                max = val;
            }
        }
        return max;
    }


    /** {@inheritDoc} */
    @Override
    public double min() {
        if ( size() == 0 ) {
            throw new IllegalStateException( "cannot find minimum of an empty list" );
        }
        double min = Double.POSITIVE_INFINITY;
        for ( int i = 0; i < _pos; i++ ) {
            double val = _data.get( i );
            if ( val < min ) {
                min = val;
            }
        }
        return min;
    }


    /** {@inheritDoc} */
    @Override
    public double sum() {
        double sum = 0;
        for ( int i = 0; i < _pos; i++ ) {
            sum += _data.get( i );
        }
        return sum;
    }


    // stringification

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder( "{" );
        for ( int i = 0, end = _pos - 1; i < end; i++ ) {
            buf.append( _data.get( i ) );
            buf.append( ", " );
        }
        if ( size() > 0 ) {
            buf.append( _data.get( _pos - 1 ) );
        }
        buf.append( "}" );
        return buf.toString();
    }


    /** TDoubleArrayList iterator */
    class TDoubleOffheapArrayIterator implements TDoubleIterator {

        /** Index of element to be returned by subsequent call to next. */
        private int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        int lastRet = -1;


        TDoubleOffheapArrayIterator( int index ) {
            cursor = index;
        }


        /** {@inheritDoc} */
        @Override
        public boolean hasNext() {
            return cursor < size();
        }


        /** {@inheritDoc} */
        @Override
        public double next() {
            try {
                double next = get( cursor );
                lastRet = cursor++;
                return next;
            } catch ( IndexOutOfBoundsException e ) {
                throw new NoSuchElementException();
            }
        }


        /** {@inheritDoc} */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        // VERSION
        out.writeByte( 0 );

        // POSITION
        out.writeInt( _pos );

        // NO_ENTRY_VALUE
        out.writeDouble( no_entry_value );

        // ENTRIES
        int len = capacity();
        out.writeInt( len );
        for( int i = 0; i < len; i++ ) {
            out.writeDouble( _data.get( i ) );
        }
    }


    @Override
    public void readExternal( ObjectInput in )
        throws IOException, ClassNotFoundException {

        // VERSION
        in.readByte();

        // POSITION
        _pos = in.readInt();

        // NO_ENTRY_VALUE
        no_entry_value = in.readDouble();

        // ENTRIES
        int len = in.readInt();
        _data = new TDoubleOffheapArray( len );
        for( int i = 0; i < len; i++ ) {
            _data.put( i, in.readDouble() );
        }
    }
} // TDoubleArrayList
