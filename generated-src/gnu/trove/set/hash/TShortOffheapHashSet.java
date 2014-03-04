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

package gnu.trove.set.hash;

import gnu.trove.set.TShortSet;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.impl.*;
import gnu.trove.impl.hash.*;
import gnu.trove.TShortCollection;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Externalizable;
import java.util.Arrays;
import java.util.Collection;

import gnu.trove.array.*;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////


/**
 * An open addressed set implementation for short primitives.
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 */

public class TShortOffheapHashSet extends TShortOffheapHash implements TShortSet, Externalizable {
	static final long serialVersionUID = 1L;


    /**
     * Creates a new <code>TShortOffheapHashSet</code> instance with the default
     * capacity and load factor.
     */
    public TShortOffheapHashSet() {
        super();
    }

    
    /**
     * Creates a new <code>TShortOffheapHashSet</code> instance with the default
     * capacity and load factor.
     */
    public TShortOffheapHashSet( int initialCapacity ) {
        super( initialCapacity );
    }


    /**
     * Creates a new <code>TShortOffheapHashSet</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TShortOffheapHashSet( int initialCapacity, float load_factor ) {
        super( initialCapacity, load_factor );
    }


    /** {@inheritDoc} */
    @Override
    public TShortIterator iterator() {
        return new TShortOffheapHashIterator( this );
    }


    /** {@inheritDoc} */
    @Override
    public short[] toArray() {
        short[] result = new short[ size() ];
        TShortOffheapArray set = _set;
        TByteOffheapArray states = _states;

        for ( int i = capacity(), j = 0; i-- > 0; ) {
            if ( states.get(i) == FULL ) {
                result[j++] = set.get( i );
            }
        }
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public short[] toArray( short[] dest ) {
        TShortOffheapArray set = _set;
        TByteOffheapArray states = _states;

        for ( int i = capacity(), j = 0; i-- > 0; ) {
            if ( states.get(i) == FULL ) {
                dest[j++] = set.get( i );
            }
        }

        if ( dest.length > _size ) {
            dest[_size] = no_entry_value;
        }
        return dest;
    }


    /** {@inheritDoc} */
    @Override
    public boolean add( short val ) {
        int index = insertKey(val);

        if ( index < 0 ) {
            return false;       // already present in set, nothing to add
        }

        postInsertHook( consumeFreeSlot );

        return true;            // yes, we added something
    }


    /** {@inheritDoc} */
    @Override
    public boolean remove( short val ) {
        int index = index(val);
        if ( index >= 0 ) {
            removeAt( index );
            return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsAll( Collection<?> collection ) {
        for ( Object element : collection ) {
            if ( element instanceof Short ) {
                short c = ( ( Short ) element ).shortValue();
                if ( ! contains( c ) ) {
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
            short element = iter.next();
            if ( ! contains( element ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsAll( short[] array ) {
        for ( int i = array.length; i-- > 0; ) {
            if ( ! contains( array[i] ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean addAll( Collection<? extends Short> collection ) {
        boolean changed = false;
        for ( Short element : collection ) {
            short e = element.shortValue();
            if ( add( e ) ) {
                changed = true;
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    @Override
    public boolean addAll( TShortCollection collection ) {
        boolean changed = false;
        TShortIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            short element = iter.next();
            if ( add( element ) ) {
                changed = true;
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    @Override
    public boolean addAll( short[] array ) {
        boolean changed = false;
        for ( int i = array.length; i-- > 0; ) {
            if ( add( array[i] ) ) {
                changed = true;
            }
        }
        return changed;
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

        _autoCompactTemporaryDisable = true;
        for ( int i = capacity(); i-- > 0; ) {
            if ( states.get(i) == FULL && ( Arrays.binarySearch( array, set.get( i ) ) < 0) ) {
                removeAt( i );
                changed = true;
            }
        }
        _autoCompactTemporaryDisable = false;

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
            if ( remove(array[i]) ) {
                changed = true;
            }
        }
        return changed;
    }


    /** {@inheritDoc} */
    @Override
    public void clear() {
        super.clear();
        TShortOffheapArray set = _set;
        TByteOffheapArray states = _states;

        for ( int i = capacity(); i-- > 0; ) {
            set.put( i, no_entry_value );
            states.put( i, FREE );
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void rehash( int newCapacity ) {
        int oldCapacity = capacity();
        
        TShortOffheapArray oldSet = _set;
        TByteOffheapArray oldStates = _states;

        _set = new TShortOffheapArray( newCapacity );
        _states = new TByteOffheapArray( newCapacity );

        for ( int i = oldCapacity; i-- > 0; ) {
            if( oldStates.get(i) == FULL ) {
                short o = oldSet.get( i );
                insertKey(o);
            }
        }
        oldSet.free();
        oldStates.free();
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals( Object other ) {
        if ( ! ( other instanceof TShortSet ) ) {
            return false;
        }
        TShortSet that = ( TShortSet ) other;
        if ( that.size() != this.size() ) {
            return false;
        }
        for ( int i = capacity(); i-- > 0; ) {
            if ( _states.get(i) == FULL ) {
                if ( ! that.contains( _set.get( i ) ) ) {
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
        for ( int i = capacity(); i-- > 0; ) {
            if ( _states.get(i) == FULL ) {
                hashcode += HashFunctions.hash( _set.get( i ) );
            }
        }
        return hashcode;
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder buffy = new StringBuilder( _size * 2 + 2 );
        buffy.append("{");
        for ( int i = capacity(), j = 1; i-- > 0; ) {
            if ( _states.get(i) == FULL ) {
                buffy.append( _set.get( i ) );
                if ( j++ < _size ) {
                    buffy.append( "," );
                }
            }
        }
        buffy.append("}");
        return buffy.toString();
    }


    class TShortOffheapHashIterator extends THashPrimitiveOffheapIterator implements TShortIterator {

        /** the collection on which the iterator operates */
        private final TShortOffheapHash _hash;

        /** {@inheritDoc} */
        public TShortOffheapHashIterator( TShortOffheapHash hash ) {
            super( hash );
            this._hash = hash;
        }

        /** {@inheritDoc} */
        @Override
        public short next() {
            moveToNextIndex();
            return _hash._set.get( _index );
        }
    }


    /** {@inheritDoc} */
    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {

    	// VERSION
    	out.writeByte( 1 );

    	// SUPER
    	super.writeExternal( out );

    	// NUMBER OF ENTRIES
    	out.writeInt( _size );

        // LOAD FACTOR -- Added version 1
        out.writeFloat( _loadFactor );

        // NO ENTRY VALUE -- Added version 1
        out.writeShort( no_entry_value );

    	// ENTRIES
        for ( int i = capacity(); i-- > 0; ) {
            if ( _states.get(i) == FULL ) {
                out.writeShort( _set.get( i ) );
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void readExternal( ObjectInput in )
    	throws IOException, ClassNotFoundException {

    	// VERSION
    	int version = in.readByte();

        // SUPER
    	super.readExternal( in );

    	// NUMBER OF ENTRIES
        int size = in.readInt();

        if ( version >= 1 ) {
            // LOAD FACTOR
            _loadFactor = in.readFloat();

            // NO ENTRY VALUE
            no_entry_value = in.readShort();
        }

    	// ENTRIES
        setUp( size );
        while ( size-- > 0 ) {
            short val = in.readShort();
            add( val );
        }
    }
} // TIntOffheapHashSet
