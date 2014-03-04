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

import gnu.trove.set.TDoubleSet;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.impl.*;
import gnu.trove.impl.hash.*;
import gnu.trove.TDoubleCollection;

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
 * An open addressed set implementation for double primitives.
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 */

public class TDoubleOffheapHashSet extends TDoubleOffheapHash implements TDoubleSet, Externalizable {
	static final long serialVersionUID = 1L;


    /**
     * Creates a new <code>TDoubleOffheapHashSet</code> instance with the default
     * capacity and load factor.
     */
    public TDoubleOffheapHashSet() {
        super();
    }

    
    /**
     * Creates a new <code>TDoubleOffheapHashSet</code> instance with the default
     * capacity and load factor.
     */
    public TDoubleOffheapHashSet( int initialCapacity ) {
        super( initialCapacity );
    }


    /**
     * Creates a new <code>TDoubleOffheapHashSet</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TDoubleOffheapHashSet( int initialCapacity, float load_factor ) {
        super( initialCapacity, load_factor );
    }


    /** {@inheritDoc} */
    @Override
    public TDoubleIterator iterator() {
        return new TDoubleOffheapHashIterator( this );
    }


    /** {@inheritDoc} */
    @Override
    public double[] toArray() {
        double[] result = new double[ size() ];
        TDoubleOffheapArray set = _set;
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
    public double[] toArray( double[] dest ) {
        TDoubleOffheapArray set = _set;
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
    public boolean add( double val ) {
        int index = insertKey(val);

        if ( index < 0 ) {
            return false;       // already present in set, nothing to add
        }

        postInsertHook( consumeFreeSlot );

        return true;            // yes, we added something
    }


    /** {@inheritDoc} */
    @Override
    public boolean remove( double val ) {
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
            if ( element instanceof Double ) {
                double c = ( ( Double ) element ).doubleValue();
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
    public boolean containsAll( TDoubleCollection collection ) {
        TDoubleIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            double element = iter.next();
            if ( ! contains( element ) ) {
                return false;
            }
        }
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean containsAll( double[] array ) {
        for ( int i = array.length; i-- > 0; ) {
            if ( ! contains( array[i] ) ) {
                return false;
            }
        }
        return true;
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
        TDoubleOffheapArray set = _set;
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
        
        TDoubleOffheapArray oldSet = _set;
        TByteOffheapArray oldStates = _states;

        _set = new TDoubleOffheapArray( newCapacity );
        _states = new TByteOffheapArray( newCapacity );

        for ( int i = oldCapacity; i-- > 0; ) {
            if( oldStates.get(i) == FULL ) {
                double o = oldSet.get( i );
                insertKey(o);
            }
        }
        oldSet.free();
        oldStates.free();
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals( Object other ) {
        if ( ! ( other instanceof TDoubleSet ) ) {
            return false;
        }
        TDoubleSet that = ( TDoubleSet ) other;
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


    class TDoubleOffheapHashIterator extends THashPrimitiveOffheapIterator implements TDoubleIterator {

        /** the collection on which the iterator operates */
        private final TDoubleOffheapHash _hash;

        /** {@inheritDoc} */
        public TDoubleOffheapHashIterator( TDoubleOffheapHash hash ) {
            super( hash );
            this._hash = hash;
        }

        /** {@inheritDoc} */
        @Override
        public double next() {
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
        out.writeDouble( no_entry_value );

    	// ENTRIES
        for ( int i = capacity(); i-- > 0; ) {
            if ( _states.get(i) == FULL ) {
                out.writeDouble( _set.get( i ) );
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
            no_entry_value = in.readDouble();
        }

    	// ENTRIES
        setUp( size );
        while ( size-- > 0 ) {
            double val = in.readDouble();
            add( val );
        }
    }
} // TIntOffheapHashSet
