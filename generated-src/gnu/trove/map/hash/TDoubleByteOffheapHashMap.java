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

package gnu.trove.map.hash;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

import gnu.trove.array.*;

import gnu.trove.map.TDoubleByteMap;
import gnu.trove.function.TByteFunction;
import gnu.trove.procedure.*;
import gnu.trove.set.*;
import gnu.trove.iterator.*;
import gnu.trove.impl.hash.*;
import gnu.trove.impl.HashFunctions;
import gnu.trove.*;

import java.io.*;
import java.util.*;

/**
 * An open addressed Map implementation for double keys and byte values.
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: _K__V_OffheapHashMap.template,v 1.1.2.16 2010/03/02 04:09:50 robeden Exp $
 */
public class TDoubleByteOffheapHashMap extends TDoubleByteOffheapHash implements TDoubleByteMap, Externalizable {
    static final long serialVersionUID = 1L;

    /** the values of the map */
    protected transient TByteOffheapArray _values;

    
    /**
     * Creates a new <code>TDoubleByteOffheapHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TDoubleByteOffheapHashMap() {
        this( DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR );
    }

    
    /**
     * Creates a new <code>TDoubleByteOffheapHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TDoubleByteOffheapHashMap( int initialCapacity ) {
        this( initialCapacity, DEFAULT_LOAD_FACTOR );
    }


    /**
     * Creates a new <code>TDoubleByteOffheapHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TDoubleByteOffheapHashMap( int initialCapacity, float loadFactor ) {
        super( initialCapacity, loadFactor );
        _values = new TByteOffheapArray( capacity() );
    }


    /**
     * rehashes the map to the new capacity.
     *
     * @param newCapacity an <code>int</code> value
     */
     /** {@inheritDoc} */
    @Override
    protected void rehash( int newCapacity ) {
        int oldCapacity = capacity();
        
        TDoubleOffheapArray oldKeys = _set;
        TByteOffheapArray oldVals = _values;
        TByteOffheapArray oldStates = _states;

        _set = new TDoubleOffheapArray( newCapacity );
        _values = new TByteOffheapArray( newCapacity );
        _states = new TByteOffheapArray( newCapacity );

        for ( int i = oldCapacity; i-- > 0; ) {
            if( oldStates.get( i ) == FULL ) {
                double o = oldKeys.get( i );
                int index = insertKey( o );
                _values.put( index, oldVals.get( i ));
            }
        }
        oldKeys.free();
        oldVals.free();
        oldStates.free();
    }


    /** {@inheritDoc} */
    @Override
    public byte put( double key, byte value ) {
        int index = insertKey( key );
        return doPut( key, value, index );
    }


    /** {@inheritDoc} */
    @Override
    public byte putIfAbsent( double key, byte value ) {
        int index = insertKey( key );
        if (index < 0)
            return _values.get( -index - 1 );
        return doPut( key, value, index );
    }


    private byte doPut( double key, byte value, int index ) {
        byte previous = no_entry_value;
        boolean isNewMapping = true;
        if ( index < 0 ) {
            index = -index -1;
            previous = _values.get( index );
            isNewMapping = false;
        }
        _values.put( index, value );

        if (isNewMapping) {
            postInsertHook( consumeFreeSlot );
        }

        return previous;
    }


    /** {@inheritDoc} */
    @Override
    public void putAll( Map<? extends Double, ? extends Byte> map ) {
        ensureCapacity( map.size() );
        // could optimize this for cases when map instanceof THashMap
        for ( Map.Entry<? extends Double, ? extends Byte> entry : map.entrySet() ) {
            this.put( entry.getKey().doubleValue(), entry.getValue().byteValue() );
        }
    }
    

    /** {@inheritDoc} */
    @Override
    public void putAll( TDoubleByteMap map ) {
        ensureCapacity( map.size() );
        TDoubleByteIterator iter = map.iterator();
        while ( iter.hasNext() ) {
            iter.advance();
            this.put( iter.key(), iter.value() );
        }
    }


    /** {@inheritDoc} */
    @Override
    public byte get( double key ) {
        int index = index( key );
        return index < 0 ? no_entry_value : _values.get( index );
    }


    /** {@inheritDoc} */
    @Override
    public void clear() {
        super.clear();
        _set.clear();
        _values.clear();
        _states.clear();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return 0 == _size;
    }


    /** {@inheritDoc} */
    @Override
    public byte remove( double key ) {
        byte prev = no_entry_value;
        int index = index( key );
        if ( index >= 0 ) {
            prev = _values.get( index );
            removeAt( index );    // clear key,state; adjust size
        }
        return prev;
    }


    /** {@inheritDoc} */
    @Override
    protected void removeAt( int index ) {
        _values.put( index, no_entry_value );
        super.removeAt( index );  // clear key, state; adjust size
    }


    /** {@inheritDoc} */
    @Override
    public TDoubleSet keySet() {
        return new TKeyView();
    }


    /** {@inheritDoc} */
    @Override
    public double[] keys() {
        double[] keys = new double[size()];
        TDoubleOffheapArray k = _set;
        TByteOffheapArray states = _states;

        for ( int i = capacity(), j = 0; i-- > 0; ) {
          if ( states.get( i ) == FULL ) {
            keys[j++] = k.get( i );
          }
        }
        return keys;
    }


    /** {@inheritDoc} */
    @Override
    public double[] keys( double[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new double[size];
        }

        TDoubleOffheapArray keys = _set;
        TByteOffheapArray states = _states;

        for ( int i = capacity(), j = 0; i-- > 0; ) {
          if ( states.get( i ) == FULL ) {
            array[j++] = keys.get( i );
          }
        }
        return array;
    }


    /** {@inheritDoc} */
    @Override
    public TByteCollection valueCollection() {
        return new TValueView();
    }


    /** {@inheritDoc} */
    @Override
    public byte[] values() {
        byte[] vals = new byte[size()];
        TByteOffheapArray v = _values;
        TByteOffheapArray states = _states;

        for ( int i = capacity(), j = 0; i-- > 0; ) {
          if ( states.get( i ) == FULL ) {
            vals[j++] = v.get( i );
          }
        }
        return vals;
    }


    /** {@inheritDoc} */
    @Override
    public byte[] values( byte[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new byte[size];
        }

        TByteOffheapArray v = _values;
        TByteOffheapArray states = _states;

        for ( int i = capacity(), j = 0; i-- > 0; ) {
          if ( states.get( i ) == FULL ) {
            array[j++] = v.get( i );
          }
        }
        return array;
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsValue( byte val ) {
        TByteOffheapArray states = _states;
        TByteOffheapArray vals = _values;

        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && val == vals.get( i ) ) {
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsKey( double key ) {
        return contains( key );
    }


    /** {@inheritDoc} */
    @Override
    public TDoubleByteIterator iterator() {
        return new TDoubleByteOffheapHashIterator( this );
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachKey( TDoubleProcedure procedure ) {
        return forEach( procedure );
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachValue( TByteProcedure procedure ) {
        TByteOffheapArray states = _states;
        TByteOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && ! procedure.execute( values.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachEntry( TDoubleByteProcedure procedure ) {
        TByteOffheapArray states = _states;
        TDoubleOffheapArray keys = _set;
        TByteOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && ! procedure.execute( keys.get( i ), values.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void transformValues( TByteFunction function ) {
        TByteOffheapArray states = _states;
        TByteOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL ) {
                values.put( i, function.execute( values.get( i ) ) );
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean retainEntries( TDoubleByteProcedure procedure ) {
        boolean modified = false;
        TByteOffheapArray states = _states;
        TDoubleOffheapArray keys = _set;
        TByteOffheapArray values = _values;


        // Temporarily disable compaction. This is a fix for bug #1738760
        tempDisableAutoCompaction();
        try {
            for ( int i = capacity(); i-- > 0; ) {
                if ( states.get( i ) == FULL && ! procedure.execute( keys.get( i ), values.get( i) ) ) {
                    removeAt( i );
                    modified = true;
                }
            }
        }
        finally {
            reenableAutoCompaction( true );
        }

        return modified;
    }


    /** {@inheritDoc} */
    @Override
    public boolean increment( double key ) {
        return adjustValue( key, ( byte ) 1 );
    }


    /** {@inheritDoc} */
    @Override
    public boolean adjustValue( double key, byte amount ) {
        int index = index( key );
        if (index < 0) {
            return false;
        } else {
            byte val = _values.get( index );
            _values.put( index, (byte)(val + amount) );
            return true;
        }
    }


    /** {@inheritDoc} */
    @Override
    public byte adjustOrPutValue( double key, byte adjust_amount, byte put_amount ) {
        int index = insertKey( key );
        final boolean isNewMapping;
        final byte newValue;
        if ( index < 0 ) {
            index = -index -1;
            newValue = (byte)(_values.get( index ) + adjust_amount);
            isNewMapping = false;
        } else {
            newValue = put_amount;
            isNewMapping = true;
        }

        _values.put( index, newValue);

        if ( isNewMapping ) {
            postInsertHook(consumeFreeSlot);
        }

        return newValue;
    }


    /** a view onto the keys of the map. */
    protected class TKeyView implements TDoubleSet {

        /** {@inheritDoc} */
        @Override
        public TDoubleIterator iterator() {
            return new TDoubleByteKeyOffheapHashIterator( TDoubleByteOffheapHashMap.this );
        }


        /** {@inheritDoc} */
        @Override
        public double getNoEntryValue() {
            return no_entry_key;
        }


        /** {@inheritDoc} */
        @Override
        public int size() {
            return _size;
        }


        /** {@inheritDoc} */
        @Override
        public boolean isEmpty() {
            return 0 == _size;
        }


        /** {@inheritDoc} */
        @Override
        public boolean contains( double entry ) {
            return TDoubleByteOffheapHashMap.this.contains( entry );
        }


        /** {@inheritDoc} */
        @Override
        public double[] toArray() {
            return TDoubleByteOffheapHashMap.this.keys();
        }


        /** {@inheritDoc} */
        @Override
        public double[] toArray( double[] dest ) {
            return TDoubleByteOffheapHashMap.this.keys( dest );
        }


        /**
         * Unsupported when operating upon a Key Set view of a TDoubleByteMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean add( double entry ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean remove( double entry ) {
            return no_entry_value != TDoubleByteOffheapHashMap.this.remove( entry );
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( Collection<?> collection ) {
            for ( Object element : collection ) {
                if ( element instanceof Double ) {
                    double ele = ( ( Double ) element ).doubleValue();
                    if ( ! TDoubleByteOffheapHashMap.this.containsKey( ele ) ) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( TDoubleCollection collection ) {
            TDoubleIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                if ( ! TDoubleByteOffheapHashMap.this.containsKey( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( double[] array ) {
            for ( double element : array ) {
                if ( ! TDoubleByteOffheapHashMap.this.contains( element ) ) {
                    return false;
                }
            }
            return true;
        }


        /**
         * Unsupported when operating upon a Key Set view of a TDoubleByteMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( Collection<? extends Double> collection ) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TDoubleByteMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( TDoubleCollection collection ) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TDoubleByteMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( double[] array ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( Collection<?> collection ) {
            boolean modified = false;
            TDoubleIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( Double.valueOf ( iter.next() ) ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( TDoubleCollection collection ) {
            if ( this == collection ) {
                return false;
            }
            boolean modified = false;
            TDoubleIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( iter.next() ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( double[] array ) {
            boolean changed = false;
            Arrays.sort( array );
            TDoubleOffheapArray set = _set;
            TByteOffheapArray states = _states;

            for ( int i = capacity(); i-- > 0; ) {
                if ( states.get( i ) == FULL && ( Arrays.binarySearch( array, set.get( i ) ) < 0) ) {
                    removeAt( i );
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( Collection<?> collection ) {
            boolean changed = false;
            for ( Object element : collection ) {
                if ( element instanceof Double ) {
                    double c = ( ( Double ) element ).doubleValue();
                    if ( remove( c ) ) {
                        changed = true;
                    }
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( TDoubleCollection collection ) {
            if ( this == collection ) {
                clear();
                return true;
            }
            boolean changed = false;
            TDoubleIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                double element = iter.next();
                if ( remove( element ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( double[] array ) {
            boolean changed = false;
            for ( int i = array.length; i-- > 0; ) {
                if ( remove( array[i] ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public void clear() {
            TDoubleByteOffheapHashMap.this.clear();
        }


        /** {@inheritDoc} */
        @Override
        public boolean forEach( TDoubleProcedure procedure ) {
            return TDoubleByteOffheapHashMap.this.forEachKey( procedure );
        }


        @Override
        public boolean equals( Object other ) {
            if (! (other instanceof TDoubleSet)) {
                return false;
            }
            final TDoubleSet that = ( TDoubleSet ) other;
            if ( that.size() != this.size() ) {
                return false;
            }
            for ( int i = capacity(); i-- > 0; ) {
                if ( _states.get( i ) == FULL ) {
                    if ( ! that.contains( _set.get( i ) ) ) {
                        return false;
                    }
                }
            }
            return true;
        }


        @Override
        public int hashCode() {
            int hashcode = 0;
            for ( int i = capacity(); i-- > 0; ) {
                if ( _states.get( i ) == FULL ) {
                    hashcode += HashFunctions.hash( _set.get( i ) );
                }
            }
            return hashcode;
        }


        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachKey( new TDoubleProcedure() {
                private boolean first = true;

                @Override
                public boolean execute( double key ) {
                    if ( first ) {
                        first = false;
                    } else {
                        buf.append( ", " );
                    }

                    buf.append( key );
                    return true;
                }
            } );
            buf.append( "}" );
            return buf.toString();
        }
    }


    /** a view onto the values of the map. */
    protected class TValueView implements TByteCollection {

        /** {@inheritDoc} */
        @Override
        public TByteIterator iterator() {
            return new TDoubleByteValueOffheapHashIterator( TDoubleByteOffheapHashMap.this );
        }


        /** {@inheritDoc} */
        @Override
        public byte getNoEntryValue() {
            return no_entry_value;
        }


        /** {@inheritDoc} */
        @Override
        public int size() {
            return _size;
        }


        /** {@inheritDoc} */
        @Override
        public boolean isEmpty() {
            return 0 == _size;
        }


        /** {@inheritDoc} */
        @Override
        public boolean contains( byte entry ) {
            return TDoubleByteOffheapHashMap.this.containsValue( entry );
        }


        /** {@inheritDoc} */
        @Override
        public byte[] toArray() {
            return TDoubleByteOffheapHashMap.this.values();
        }


        /** {@inheritDoc} */
        @Override
        public byte[] toArray( byte[] dest ) {
            return TDoubleByteOffheapHashMap.this.values( dest );
        }



        @Override
        public boolean add( byte entry ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean remove( byte entry ) {
            TByteOffheapArray values = _values;
            TByteOffheapArray states = _states;

            for ( int i = capacity(); i-- > 0; ) {
                byte state = states.get( i );
                if ( ( state != FREE && state != REMOVED ) && entry == values.get( i ) ) {
                    removeAt( i );
                    return true;
                }
            }
            return false;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( Collection<?> collection ) {
            for ( Object element : collection ) {
                if ( element instanceof Byte ) {
                    byte ele = ( ( Byte ) element ).byteValue();
                    if ( ! TDoubleByteOffheapHashMap.this.containsValue( ele ) ) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( TByteCollection collection ) {
            TByteIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                if ( ! TDoubleByteOffheapHashMap.this.containsValue( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( byte[] array ) {
            for ( byte element : array ) {
                if ( ! TDoubleByteOffheapHashMap.this.containsValue( element ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean addAll( Collection<? extends Byte> collection ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean addAll( TByteCollection collection ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean addAll( byte[] array ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( Collection<?> collection ) {
            boolean modified = false;
            TByteIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( Byte.valueOf ( iter.next() ) ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( TByteCollection collection ) {
            if ( this == collection ) {
                return false;
            }
            boolean modified = false;
            TByteIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( iter.next() ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( byte[] array ) {
            boolean changed = false;
            Arrays.sort( array );
            TByteOffheapArray values = _values;
            TByteOffheapArray states = _states;

            for ( int i = capacity(); i-- > 0; ) {
                if ( states.get( i ) == FULL && ( Arrays.binarySearch( array, values.get( i ) ) < 0) ) {
                    removeAt( i );
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( Collection<?> collection ) {
            boolean changed = false;
            for ( Object element : collection ) {
                if ( element instanceof Byte ) {
                    byte c = ( ( Byte ) element ).byteValue();
                    if ( remove( c ) ) {
                        changed = true;
                    }
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( TByteCollection collection ) {
            if ( this == collection ) {
                clear();
                return true;
            }
            boolean changed = false;
            TByteIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                byte element = iter.next();
                if ( remove( element ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( byte[] array ) {
            boolean changed = false;
            for ( int i = array.length; i-- > 0; ) {
                if ( remove( array[i] ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public void clear() {
            TDoubleByteOffheapHashMap.this.clear();
        }


        /** {@inheritDoc} */
        @Override
        public boolean forEach( TByteProcedure procedure ) {
            return TDoubleByteOffheapHashMap.this.forEachValue( procedure );
        }


        /** {@inheritDoc} */
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachValue( new TByteProcedure() {
                private boolean first = true;

                @Override
                public boolean execute( byte value ) {
                    if ( first ) {
                        first = false;
                    } else {
                        buf.append( ", " );
                    }

                    buf.append( value );
                    return true;
                }
            } );
            buf.append( "}" );
            return buf.toString();
        }
    }


    class TDoubleByteKeyOffheapHashIterator extends THashPrimitiveOffheapIterator implements TDoubleIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveOffheapHash</tt> we will be iterating over.
         */
        TDoubleByteKeyOffheapHashIterator( TPrimitiveOffheapHash hash ) {
            super( hash );
        }

        /** {@inheritDoc} */
        @Override
        public double next() {
            moveToNextIndex();
            return _set.get( _index );
        }

        /** @{inheritDoc} */
        @Override
        public void remove() {
            if ( _expectedSize != _hash.size() ) {
                throw new ConcurrentModificationException();
            }

            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TDoubleByteOffheapHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }

            _expectedSize--;
        }
    }


   
    class TDoubleByteValueOffheapHashIterator extends THashPrimitiveOffheapIterator implements TByteIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveOffheapHash</tt> we will be iterating over.
         */
        TDoubleByteValueOffheapHashIterator( TPrimitiveOffheapHash hash ) {
            super( hash );
        }

        /** {@inheritDoc} */
        @Override
        public byte next() {
            moveToNextIndex();
            return _values.get( _index );
        }

        /** @{inheritDoc} */
        @Override
        public void remove() {
            if ( _expectedSize != _hash.size() ) {
                throw new ConcurrentModificationException();
            }

            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TDoubleByteOffheapHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }

            _expectedSize--;
        }
    }


    class TDoubleByteOffheapHashIterator extends THashPrimitiveOffheapIterator implements TDoubleByteIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param map the <tt>TDoubleByteOffheapHashMap</tt> we will be iterating over.
         */
        TDoubleByteOffheapHashIterator( TDoubleByteOffheapHashMap map ) {
            super( map );
        }

        /** {@inheritDoc} */
        @Override
        public void advance() {
            moveToNextIndex();
        }

        /** {@inheritDoc} */
        @Override
        public double key() {
            return _set.get( _index );
        }

        /** {@inheritDoc} */
        @Override
        public byte value() {
            return _values.get( _index );
        }

        /** {@inheritDoc} */
        @Override
        public byte setValue( byte val ) {
            byte old = value();
            _values.put( _index, val );
            return old;
        }

        /** @{inheritDoc} */
        @Override
        public void remove() {
            if ( _expectedSize != _hash.size() ) {
                throw new ConcurrentModificationException();
            }
            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TDoubleByteOffheapHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }
            _expectedSize--;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals( Object other ) {
        if ( ! ( other instanceof TDoubleByteMap ) ) {
            return false;
        }
        TDoubleByteMap that = ( TDoubleByteMap ) other;
        if ( that.size() != this.size() ) {
            return false;
        }
        TByteOffheapArray values = _values;
        TByteOffheapArray states = _states;
        byte this_no_entry_value = getNoEntryValue();
        byte that_no_entry_value = that.getNoEntryValue();
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL ) {
                double key = _set.get( i );
                byte that_value = that.get( key );
                byte this_value = values.get( i );
                if ( ( this_value != that_value ) &&
                     ( this_value != this_no_entry_value ) &&
                     ( that_value != that_no_entry_value ) ) {
                    return false;
                }
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hashcode = 0;
        TByteOffheapArray states = _states;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL ) {
                hashcode += HashFunctions.hash( _set.get( i ) ) ^
                            HashFunctions.hash( _values.get( i ) );
            }
        }
        return hashcode;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder( "{" );
        forEachEntry( new TDoubleByteProcedure() {
            private boolean first = true;
            @Override
            public boolean execute( double key, byte value ) {
                if ( first ) first = false;
                else buf.append( ", " );

                buf.append(key);
                buf.append("=");
                buf.append(value);
                return true;
            }
        });
        buf.append( "}" );
        return buf.toString();
    }


    /** {@inheritDoc} */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // VERSION
    	out.writeByte( 0 );

        // SUPER
    	super.writeExternal( out );

    	// NUMBER OF ENTRIES
    	out.writeInt( _size );

    	// ENTRIES
    	for ( int i = capacity(); i-- > 0; ) {
            if ( _states.get( i ) == FULL ) {
                out.writeDouble( _set.get( i ) );
                out.writeByte( _values.get( i ) );
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // VERSION
    	in.readByte();

        // SUPER
    	super.readExternal( in );

    	// NUMBER OF ENTRIES
    	int size = in.readInt();
    	setUp( size );

    	// ENTRIES
        while (size-- > 0) {
            double key = in.readDouble();
            byte val = in.readByte();
            put(key, val);
        }
    }
} // TDoubleByteOffheapHashMap
