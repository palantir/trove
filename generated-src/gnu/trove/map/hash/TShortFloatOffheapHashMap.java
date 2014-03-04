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

import gnu.trove.map.TShortFloatMap;
import gnu.trove.function.TFloatFunction;
import gnu.trove.procedure.*;
import gnu.trove.set.*;
import gnu.trove.iterator.*;
import gnu.trove.impl.hash.*;
import gnu.trove.impl.HashFunctions;
import gnu.trove.*;

import java.io.*;
import java.util.*;

/**
 * An open addressed Map implementation for short keys and float values.
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: _K__V_OffheapHashMap.template,v 1.1.2.16 2010/03/02 04:09:50 robeden Exp $
 */
public class TShortFloatOffheapHashMap extends TShortFloatOffheapHash implements TShortFloatMap, Externalizable {
    static final long serialVersionUID = 1L;

    /** the values of the map */
    protected transient TFloatOffheapArray _values;

    
    /**
     * Creates a new <code>TShortFloatOffheapHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TShortFloatOffheapHashMap() {
        this( DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR );
    }

    
    /**
     * Creates a new <code>TShortFloatOffheapHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TShortFloatOffheapHashMap( int initialCapacity ) {
        this( initialCapacity, DEFAULT_LOAD_FACTOR );
    }


    /**
     * Creates a new <code>TShortFloatOffheapHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TShortFloatOffheapHashMap( int initialCapacity, float loadFactor ) {
        super( initialCapacity, loadFactor );
        _values = new TFloatOffheapArray( capacity() );
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
        
        TShortOffheapArray oldKeys = _set;
        TFloatOffheapArray oldVals = _values;
        TByteOffheapArray oldStates = _states;

        _set = new TShortOffheapArray( newCapacity );
        _values = new TFloatOffheapArray( newCapacity );
        _states = new TByteOffheapArray( newCapacity );

        for ( int i = oldCapacity; i-- > 0; ) {
            if( oldStates.get( i ) == FULL ) {
                short o = oldKeys.get( i );
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
    public float put( short key, float value ) {
        int index = insertKey( key );
        return doPut( key, value, index );
    }


    /** {@inheritDoc} */
    @Override
    public float putIfAbsent( short key, float value ) {
        int index = insertKey( key );
        if (index < 0)
            return _values.get( -index - 1 );
        return doPut( key, value, index );
    }


    private float doPut( short key, float value, int index ) {
        float previous = no_entry_value;
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
    public void putAll( Map<? extends Short, ? extends Float> map ) {
        ensureCapacity( map.size() );
        // could optimize this for cases when map instanceof THashMap
        for ( Map.Entry<? extends Short, ? extends Float> entry : map.entrySet() ) {
            this.put( entry.getKey().shortValue(), entry.getValue().floatValue() );
        }
    }
    

    /** {@inheritDoc} */
    @Override
    public void putAll( TShortFloatMap map ) {
        ensureCapacity( map.size() );
        TShortFloatIterator iter = map.iterator();
        while ( iter.hasNext() ) {
            iter.advance();
            this.put( iter.key(), iter.value() );
        }
    }


    /** {@inheritDoc} */
    @Override
    public float get( short key ) {
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
    public float remove( short key ) {
        float prev = no_entry_value;
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
    public TShortSet keySet() {
        return new TKeyView();
    }


    /** {@inheritDoc} */
    @Override
    public short[] keys() {
        short[] keys = new short[size()];
        TShortOffheapArray k = _set;
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
    public short[] keys( short[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new short[size];
        }

        TShortOffheapArray keys = _set;
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
    public TFloatCollection valueCollection() {
        return new TValueView();
    }


    /** {@inheritDoc} */
    @Override
    public float[] values() {
        float[] vals = new float[size()];
        TFloatOffheapArray v = _values;
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
    public float[] values( float[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new float[size];
        }

        TFloatOffheapArray v = _values;
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
    public boolean containsValue( float val ) {
        TByteOffheapArray states = _states;
        TFloatOffheapArray vals = _values;

        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && val == vals.get( i ) ) {
                return true;
            }
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsKey( short key ) {
        return contains( key );
    }


    /** {@inheritDoc} */
    @Override
    public TShortFloatIterator iterator() {
        return new TShortFloatOffheapHashIterator( this );
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachKey( TShortProcedure procedure ) {
        return forEach( procedure );
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachValue( TFloatProcedure procedure ) {
        TByteOffheapArray states = _states;
        TFloatOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && ! procedure.execute( values.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean forEachEntry( TShortFloatProcedure procedure ) {
        TByteOffheapArray states = _states;
        TShortOffheapArray keys = _set;
        TFloatOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL && ! procedure.execute( keys.get( i ), values.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void transformValues( TFloatFunction function ) {
        TByteOffheapArray states = _states;
        TFloatOffheapArray values = _values;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL ) {
                values.put( i, function.execute( values.get( i ) ) );
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean retainEntries( TShortFloatProcedure procedure ) {
        boolean modified = false;
        TByteOffheapArray states = _states;
        TShortOffheapArray keys = _set;
        TFloatOffheapArray values = _values;


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
    public boolean increment( short key ) {
        return adjustValue( key, ( float ) 1 );
    }


    /** {@inheritDoc} */
    @Override
    public boolean adjustValue( short key, float amount ) {
        int index = index( key );
        if (index < 0) {
            return false;
        } else {
            float val = _values.get( index );
            _values.put( index, (float)(val + amount) );
            return true;
        }
    }


    /** {@inheritDoc} */
    @Override
    public float adjustOrPutValue( short key, float adjust_amount, float put_amount ) {
        int index = insertKey( key );
        final boolean isNewMapping;
        final float newValue;
        if ( index < 0 ) {
            index = -index -1;
            newValue = (float)(_values.get( index ) + adjust_amount);
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
    protected class TKeyView implements TShortSet {

        /** {@inheritDoc} */
        @Override
        public TShortIterator iterator() {
            return new TShortFloatKeyOffheapHashIterator( TShortFloatOffheapHashMap.this );
        }


        /** {@inheritDoc} */
        @Override
        public short getNoEntryValue() {
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
        public boolean contains( short entry ) {
            return TShortFloatOffheapHashMap.this.contains( entry );
        }


        /** {@inheritDoc} */
        @Override
        public short[] toArray() {
            return TShortFloatOffheapHashMap.this.keys();
        }


        /** {@inheritDoc} */
        @Override
        public short[] toArray( short[] dest ) {
            return TShortFloatOffheapHashMap.this.keys( dest );
        }


        /**
         * Unsupported when operating upon a Key Set view of a TShortFloatMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean add( short entry ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean remove( short entry ) {
            return no_entry_value != TShortFloatOffheapHashMap.this.remove( entry );
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( Collection<?> collection ) {
            for ( Object element : collection ) {
                if ( element instanceof Short ) {
                    short ele = ( ( Short ) element ).shortValue();
                    if ( ! TShortFloatOffheapHashMap.this.containsKey( ele ) ) {
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
        public boolean containsAll( TShortCollection collection ) {
            TShortIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                if ( ! TShortFloatOffheapHashMap.this.containsKey( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( short[] array ) {
            for ( short element : array ) {
                if ( ! TShortFloatOffheapHashMap.this.contains( element ) ) {
                    return false;
                }
            }
            return true;
        }


        /**
         * Unsupported when operating upon a Key Set view of a TShortFloatMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( Collection<? extends Short> collection ) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TShortFloatMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( TShortCollection collection ) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TShortFloatMap
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public boolean addAll( short[] array ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( Collection<?> collection ) {
            boolean modified = false;
            TShortIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( Short.valueOf ( iter.next() ) ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( TShortCollection collection ) {
            if ( this == collection ) {
                return false;
            }
            boolean modified = false;
            TShortIterator iter = iterator();
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
        public boolean retainAll( short[] array ) {
            boolean changed = false;
            Arrays.sort( array );
            TShortOffheapArray set = _set;
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
                if ( element instanceof Short ) {
                    short c = ( ( Short ) element ).shortValue();
                    if ( remove( c ) ) {
                        changed = true;
                    }
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( TShortCollection collection ) {
            if ( this == collection ) {
                clear();
                return true;
            }
            boolean changed = false;
            TShortIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                short element = iter.next();
                if ( remove( element ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( short[] array ) {
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
            TShortFloatOffheapHashMap.this.clear();
        }


        /** {@inheritDoc} */
        @Override
        public boolean forEach( TShortProcedure procedure ) {
            return TShortFloatOffheapHashMap.this.forEachKey( procedure );
        }


        @Override
        public boolean equals( Object other ) {
            if (! (other instanceof TShortSet)) {
                return false;
            }
            final TShortSet that = ( TShortSet ) other;
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
            forEachKey( new TShortProcedure() {
                private boolean first = true;

                @Override
                public boolean execute( short key ) {
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
    protected class TValueView implements TFloatCollection {

        /** {@inheritDoc} */
        @Override
        public TFloatIterator iterator() {
            return new TShortFloatValueOffheapHashIterator( TShortFloatOffheapHashMap.this );
        }


        /** {@inheritDoc} */
        @Override
        public float getNoEntryValue() {
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
        public boolean contains( float entry ) {
            return TShortFloatOffheapHashMap.this.containsValue( entry );
        }


        /** {@inheritDoc} */
        @Override
        public float[] toArray() {
            return TShortFloatOffheapHashMap.this.values();
        }


        /** {@inheritDoc} */
        @Override
        public float[] toArray( float[] dest ) {
            return TShortFloatOffheapHashMap.this.values( dest );
        }



        @Override
        public boolean add( float entry ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean remove( float entry ) {
            TFloatOffheapArray values = _values;
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
                if ( element instanceof Float ) {
                    float ele = ( ( Float ) element ).floatValue();
                    if ( ! TShortFloatOffheapHashMap.this.containsValue( ele ) ) {
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
        public boolean containsAll( TFloatCollection collection ) {
            TFloatIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                if ( ! TShortFloatOffheapHashMap.this.containsValue( iter.next() ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean containsAll( float[] array ) {
            for ( float element : array ) {
                if ( ! TShortFloatOffheapHashMap.this.containsValue( element ) ) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        @Override
        public boolean addAll( Collection<? extends Float> collection ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean addAll( TFloatCollection collection ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean addAll( float[] array ) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( Collection<?> collection ) {
            boolean modified = false;
            TFloatIterator iter = iterator();
            while ( iter.hasNext() ) {
                if ( ! collection.contains( Float.valueOf ( iter.next() ) ) ) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        /** {@inheritDoc} */
        @Override
        public boolean retainAll( TFloatCollection collection ) {
            if ( this == collection ) {
                return false;
            }
            boolean modified = false;
            TFloatIterator iter = iterator();
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
        public boolean retainAll( float[] array ) {
            boolean changed = false;
            Arrays.sort( array );
            TFloatOffheapArray values = _values;
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
                if ( element instanceof Float ) {
                    float c = ( ( Float ) element ).floatValue();
                    if ( remove( c ) ) {
                        changed = true;
                    }
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( TFloatCollection collection ) {
            if ( this == collection ) {
                clear();
                return true;
            }
            boolean changed = false;
            TFloatIterator iter = collection.iterator();
            while ( iter.hasNext() ) {
                float element = iter.next();
                if ( remove( element ) ) {
                    changed = true;
                }
            }
            return changed;
        }


        /** {@inheritDoc} */
        @Override
        public boolean removeAll( float[] array ) {
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
            TShortFloatOffheapHashMap.this.clear();
        }


        /** {@inheritDoc} */
        @Override
        public boolean forEach( TFloatProcedure procedure ) {
            return TShortFloatOffheapHashMap.this.forEachValue( procedure );
        }


        /** {@inheritDoc} */
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachValue( new TFloatProcedure() {
                private boolean first = true;

                @Override
                public boolean execute( float value ) {
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


    class TShortFloatKeyOffheapHashIterator extends THashPrimitiveOffheapIterator implements TShortIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveOffheapHash</tt> we will be iterating over.
         */
        TShortFloatKeyOffheapHashIterator( TPrimitiveOffheapHash hash ) {
            super( hash );
        }

        /** {@inheritDoc} */
        @Override
        public short next() {
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
                TShortFloatOffheapHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }

            _expectedSize--;
        }
    }


   
    class TShortFloatValueOffheapHashIterator extends THashPrimitiveOffheapIterator implements TFloatIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveOffheapHash</tt> we will be iterating over.
         */
        TShortFloatValueOffheapHashIterator( TPrimitiveOffheapHash hash ) {
            super( hash );
        }

        /** {@inheritDoc} */
        @Override
        public float next() {
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
                TShortFloatOffheapHashMap.this.removeAt( _index );
            }
            finally {
                _hash.reenableAutoCompaction( false );
            }

            _expectedSize--;
        }
    }


    class TShortFloatOffheapHashIterator extends THashPrimitiveOffheapIterator implements TShortFloatIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param map the <tt>TShortFloatOffheapHashMap</tt> we will be iterating over.
         */
        TShortFloatOffheapHashIterator( TShortFloatOffheapHashMap map ) {
            super( map );
        }

        /** {@inheritDoc} */
        @Override
        public void advance() {
            moveToNextIndex();
        }

        /** {@inheritDoc} */
        @Override
        public short key() {
            return _set.get( _index );
        }

        /** {@inheritDoc} */
        @Override
        public float value() {
            return _values.get( _index );
        }

        /** {@inheritDoc} */
        @Override
        public float setValue( float val ) {
            float old = value();
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
                TShortFloatOffheapHashMap.this.removeAt( _index );
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
        if ( ! ( other instanceof TShortFloatMap ) ) {
            return false;
        }
        TShortFloatMap that = ( TShortFloatMap ) other;
        if ( that.size() != this.size() ) {
            return false;
        }
        TFloatOffheapArray values = _values;
        TByteOffheapArray states = _states;
        float this_no_entry_value = getNoEntryValue();
        float that_no_entry_value = that.getNoEntryValue();
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get( i ) == FULL ) {
                short key = _set.get( i );
                float that_value = that.get( key );
                float this_value = values.get( i );
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
        forEachEntry( new TShortFloatProcedure() {
            private boolean first = true;
            @Override
            public boolean execute( short key, float value ) {
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
                out.writeShort( _set.get( i ) );
                out.writeFloat( _values.get( i ) );
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
            short key = in.readShort();
            float val = in.readFloat();
            put(key, val);
        }
    }
} // TShortFloatOffheapHashMap
