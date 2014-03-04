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

import gnu.trove.map.TCharDoubleMap;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.procedure.*;
import gnu.trove.set.*;
import gnu.trove.iterator.*;
import gnu.trove.impl.hash.*;
import gnu.trove.impl.HashFunctions;
import gnu.trove.*;

import java.io.*;
import java.util.*;

/**
 * An open addressed Map implementation for char keys and double values.
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: _K__V_OffheapHashMap.template,v 1.1.2.16 2010/03/02 04:09:50 robeden Exp $
 */
public class TCharDoubleOffheapHashMap extends TCharDoubleOffheapHash implements TCharDoubleMap, Externalizable {
    static final long serialVersionUID = 1L;

    /** the values of the map */
    protected transient TDoubleOffheapArray _values;

    
    /**
     * Creates a new <code>TCharDoubleOffheapHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TCharDoubleOffheapHashMap() {
        this( DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR );
    }

    
    /**
     * Creates a new <code>TCharDoubleOffheapHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TCharDoubleOffheapHashMap( int initialCapacity ) {
        this( initialCapacity, DEFAULT_LOAD_FACTOR );
    }


    /**
     * Creates a new <code>TCharDoubleOffheapHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TCharDoubleOffheapHashMap( int initialCapacity, float loadFactor ) {
        super( initialCapacity, loadFactor );
        _values = new TDoubleOffheapArray( capacity() );
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
        
        TCharOffheapArray oldKeys = _set;
        TDoubleOffheapArray oldVals = _values;
        TByteOffheapArray oldStates = _states;

        _set = new TCharOffheapArray( newCapacity );
        _values = new TDoubleOffheapArray( newCapacity );
        _states = new TByteOffheapArray( newCapacity );

        for ( int i = oldCapacity; i-- > 0; ) {
            if( oldStates.get( i ) == FULL ) {
                char o = oldKeys.get( i );
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
    public double put( char key, double value ) {
        int index = insertKey( key );
        return doPut( key, value, index );
    }


    /** {@inheritDoc} */
    @Override
    public double putIfAbsent( char key, double value ) {
        int index = insertKey( key );
        if (index < 0)
            return _values.get( -index - 1 );
        return doPut( key, value, index );
    }


    private double doPut( char key, double value, int index ) {
        double previous = no_entry_value;
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
    public void putAll( Map<? extends Character, ? extends Double> map ) {
        ensureCapacity( map.size() );
        // could optimize this for cases when map instanceof THashMap
        for ( Map.Entry<? extends Character, ? extends Double> entry : map.entrySet() ) {
            this.put( entry.getKey().charValue(), entry.getValue().doubleValue() );
        }
    }
    

    /** {@inheritDoc} */
    @Override
    public void putAll( TCharDoubleMap map ) {
        ensureCapacity( map.size() );
        TCharDoubleIterator iter = map.iterator();
        while ( iter.hasNext() ) {
            iter.advance();
            this.put( iter.key(), iter.value() );
        }
    }


    /** {@inheritDoc} */
    @Override
    public double get( char key ) {
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
    public double remove( char key ) {
        double prev = no_entry_value;
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
    public TCharSet keySet() {
        return new TKeyView();
    }


    /** {@inheritDoc} */
    @Override
    public char[] keys() {
        char[] keys = new char[size()];
        TCharOffheapArray k = _set;
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
    public char[] keys( char[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new char[size];
        }

        TCharOffheapArray keys = _set;
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
    public TDoubleCollection valueCollection() {
        return new TValueView();
    }


    /** {@inheritDoc} */
    @Override
    public double[] values() {
        double[] vals = new double[size()];
        TDoubleOffheapArray v = _values;
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
    public double[] values( double[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new double[size];
        }

        TDoubleOffheapArray v = _values;
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
    public boolean containsValue( double val ) {
        TByteOffheapArray states = _states;
        TDoubleOffheapArray vals = _values;

        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && val == vals.get( i ) ) {
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsKey( char key ) {
        return contains( key );
    }


    /** {@inheritDoc} */
    @Override
    public TCharDoubleIterator iterator() {
        return new TCharDoubleOffheapHashIterator( this );
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachKey( TCharProcedure procedure ) {
        return forEach( procedure );
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachValue( TDoubleProcedure procedure ) {
        TByteOffheapArray states = _states;
        TDoubleOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && ! procedure.execute( values.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachEntry( TCharDoubleProcedure procedure ) {
        TByteOffheapArray states = _states;
        TCharOffheapArray keys = _set;
        TDoubleOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && ! procedure.execute( keys.get( i ), values.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void transformValues( TDoubleFunction function ) {
        TByteOffheapArray states = _states;
        TDoubleOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL ) {
                values.put( i, function.execute( values.get( i ) ) );
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean retainEntries( TCharDoubleProcedure procedure ) {
        boolean modified = false;
        TByteOffheapArray states = _states;
        TCharOffheapArray keys = _set;
        TDoubleOffheapArray values = _values;


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
    public boolean increment( char key ) {
        return adjustValue( key, ( double ) 1 );
    }


    /** {@inheritDoc} */
    @Override
    public boolean adjustValue( char key, double amount ) {
        int index = index( key );
        if (index < 0) {
            return false;
        } else {
            double val = _values.get( index );
            _values.put( index, (double)(val + amount) );
            return true;
        }
    }


    /** {@inheritDoc} */
    @Override
    public double adjustOrPutValue( char key, double adjust_amount, double put_amount ) {
        int index = insertKey( key );
        final boolean isNewMapping;
        final double newValue;
        if ( index < 0 ) {
            index = -index -1;
            newValue = (double)(_values.get( index ) + adjust_amount);
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
    protected class TKeyView implements TCharSet {

        /** {@inheritDoc} */
        @Override
        public TCharIterator iterator() {
            return new TCharDoubleKeyOffheapHashIterator( TCharDoubleOffheapHashMap.this );
        }


        /** {@inheritDoc} */
        @Override
        public char getNoEntryValue() {
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
        public boolean contains( char entry ) {
            return TCharDoubleOffheapHashMap.this.contains( entry );
        }


        /** {@inheritDoc} */
        @Override
        public char[] toArray() {
            return TCharDoubleOffheapHashMap.this.keys();
        }


        /** {@inheritDoc} */
        @Override
        public char[] toArray( char[] dest ) {
            return TCharDoubleOffheapHashMap.this.keys( dest );
        }


        /**
         * Unsupported when operating upon a Key Set view of a TCharDoubleMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean add( char entry ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean remove( char entry ) {
            return no_entry_value != TCharDoubleOffheapHashMap.this.remove( entry );
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( Collection<?> collection ) {
            for ( Object element : collection ) {
                if ( element instanceof Character ) {
                    char ele = ( ( Character ) element ).charValue();
                    if ( ! TCharDoubleOffheapHashMap.this.containsKey( ele ) ) {
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
        public boolean containsAll( TCharCollection collection ) {
            TCharIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                if ( ! TCharDoubleOffheapHashMap.this.containsKey( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( char[] array ) {
            for ( char element : array ) {
                if ( ! TCharDoubleOffheapHashMap.this.contains( element ) ) {
                    return false;
                }
            }
            return true;
        }


        /**
         * Unsupported when operating upon a Key Set view of a TCharDoubleMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( Collection<? extends Character> collection ) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TCharDoubleMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( TCharCollection collection ) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TCharDoubleMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( char[] array ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( Collection<?> collection ) {
            boolean modified = false;
            TCharIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( Character.valueOf ( iter.next() ) ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( TCharCollection collection ) {
            if ( this == collection ) {
                return false;
            }
            boolean modified = false;
            TCharIterator iter = iterator();
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
        public boolean retainAll( char[] array ) {
            boolean changed = false;
            Arrays.sort( array );
            TCharOffheapArray set = _set;
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
                if ( element instanceof Character ) {
                    char c = ( ( Character ) element ).charValue();
                    if ( remove( c ) ) {
                        changed = true;
                    }
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( TCharCollection collection ) {
            if ( this == collection ) {
                clear();
                return true;
            }
            boolean changed = false;
            TCharIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                char element = iter.next();
                if ( remove( element ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( char[] array ) {
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
            TCharDoubleOffheapHashMap.this.clear();
        }


        /** {@inheritDoc} */
        @Override
        public boolean forEach( TCharProcedure procedure ) {
            return TCharDoubleOffheapHashMap.this.forEachKey( procedure );
        }


        @Override
        public boolean equals( Object other ) {
            if (! (other instanceof TCharSet)) {
                return false;
            }
            final TCharSet that = ( TCharSet ) other;
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
            forEachKey( new TCharProcedure() {
                private boolean first = true;

                @Override
                public boolean execute( char key ) {
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
    protected class TValueView implements TDoubleCollection {

        /** {@inheritDoc} */
        @Override
        public TDoubleIterator iterator() {
            return new TCharDoubleValueOffheapHashIterator( TCharDoubleOffheapHashMap.this );
        }


        /** {@inheritDoc} */
        @Override
        public double getNoEntryValue() {
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
        public boolean contains( double entry ) {
            return TCharDoubleOffheapHashMap.this.containsValue( entry );
        }


        /** {@inheritDoc} */
        @Override
        public double[] toArray() {
            return TCharDoubleOffheapHashMap.this.values();
        }


        /** {@inheritDoc} */
        @Override
        public double[] toArray( double[] dest ) {
            return TCharDoubleOffheapHashMap.this.values( dest );
        }



        @Override
        public boolean add( double entry ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean remove( double entry ) {
            TDoubleOffheapArray values = _values;
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
                if ( element instanceof Double ) {
                    double ele = ( ( Double ) element ).doubleValue();
                    if ( ! TCharDoubleOffheapHashMap.this.containsValue( ele ) ) {
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
                if ( ! TCharDoubleOffheapHashMap.this.containsValue( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( double[] array ) {
            for ( double element : array ) {
                if ( ! TCharDoubleOffheapHashMap.this.containsValue( element ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean addAll( Collection<? extends Double> collection ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean addAll( TDoubleCollection collection ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
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
            TDoubleOffheapArray values = _values;
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
            TCharDoubleOffheapHashMap.this.clear();
        }


        /** {@inheritDoc} */
        @Override
        public boolean forEach( TDoubleProcedure procedure ) {
            return TCharDoubleOffheapHashMap.this.forEachValue( procedure );
        }


        /** {@inheritDoc} */
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachValue( new TDoubleProcedure() {
                private boolean first = true;

                @Override
                public boolean execute( double value ) {
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


    class TCharDoubleKeyOffheapHashIterator extends THashPrimitiveOffheapIterator implements TCharIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveOffheapHash</tt> we will be iterating over.
         */
        TCharDoubleKeyOffheapHashIterator( TPrimitiveOffheapHash hash ) {
            super( hash );
        }

        /** {@inheritDoc} */
        @Override
        public char next() {
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
                TCharDoubleOffheapHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }

            _expectedSize--;
        }
    }


   
    class TCharDoubleValueOffheapHashIterator extends THashPrimitiveOffheapIterator implements TDoubleIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveOffheapHash</tt> we will be iterating over.
         */
        TCharDoubleValueOffheapHashIterator( TPrimitiveOffheapHash hash ) {
            super( hash );
        }

        /** {@inheritDoc} */
        @Override
        public double next() {
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
                TCharDoubleOffheapHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }

            _expectedSize--;
        }
    }


    class TCharDoubleOffheapHashIterator extends THashPrimitiveOffheapIterator implements TCharDoubleIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param map the <tt>TCharDoubleOffheapHashMap</tt> we will be iterating over.
         */
        TCharDoubleOffheapHashIterator( TCharDoubleOffheapHashMap map ) {
            super( map );
        }

        /** {@inheritDoc} */
        @Override
        public void advance() {
            moveToNextIndex();
        }

        /** {@inheritDoc} */
        @Override
        public char key() {
            return _set.get( _index );
        }

        /** {@inheritDoc} */
        @Override
        public double value() {
            return _values.get( _index );
        }

        /** {@inheritDoc} */
        @Override
        public double setValue( double val ) {
            double old = value();
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
                TCharDoubleOffheapHashMap.this.removeAt( _index );
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
        if ( ! ( other instanceof TCharDoubleMap ) ) {
            return false;
        }
        TCharDoubleMap that = ( TCharDoubleMap ) other;
        if ( that.size() != this.size() ) {
            return false;
        }
        TDoubleOffheapArray values = _values;
        TByteOffheapArray states = _states;
        double this_no_entry_value = getNoEntryValue();
        double that_no_entry_value = that.getNoEntryValue();
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL ) {
                char key = _set.get( i );
                double that_value = that.get( key );
                double this_value = values.get( i );
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
        forEachEntry( new TCharDoubleProcedure() {
            private boolean first = true;
            @Override
            public boolean execute( char key, double value ) {
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
                out.writeChar( _set.get( i ) );
                out.writeDouble( _values.get( i ) );
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
            char key = in.readChar();
            double val = in.readDouble();
            put(key, val);
        }
    }
} // TCharDoubleOffheapHashMap
