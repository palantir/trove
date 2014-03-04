///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2008, Robert D. Eden All Rights Reserved.
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

package gnu.trove.impl.unmodifiable;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

////////////////////////////////////////////////////////////
// THIS IS AN IMPLEMENTATION CLASS. DO NOT USE DIRECTLY!  //
// Access to these methods should be through TCollections //
////////////////////////////////////////////////////////////


import gnu.trove.iterator.*;
import gnu.trove.procedure.*;
import gnu.trove.set.*;
import gnu.trove.list.*;
import gnu.trove.function.*;
import gnu.trove.map.*;
import gnu.trove.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Random;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;


public class TUnmodifiableFloatList extends TUnmodifiableFloatCollection implements TFloatList {
	static final long serialVersionUID = -283967356065247728L;

	final TFloatList list;

	public TUnmodifiableFloatList( TFloatList list ) {
		super( list );
		this.list = list;
	}

    @Override
	public boolean equals( Object o )   { return o == this || list.equals( o ); }
    @Override
	public int hashCode()               { return list.hashCode(); }

    @Override
	public float get( int index )         { return list.get( index ); }
    @Override
	public int indexOf( float o )         { return list.indexOf( o ); }
    @Override
	public int lastIndexOf( float o )     { return list.lastIndexOf( o ); }

    @Override
	public float[] toArray( int offset, int len ) {
		return list.toArray( offset, len );
	}
    @Override
	public float[] toArray( float[] dest, int offset, int len ) {
		return list.toArray( dest, offset, len );
	}
    @Override
	public float[] toArray( float[] dest, int source_pos, int dest_pos, int len ) {
		return list.toArray( dest, source_pos, dest_pos, len );
	}

    @Override
	public boolean forEachDescending( TFloatProcedure procedure ) {
		return list.forEachDescending( procedure );
	}

    @Override
	public int binarySearch( float value ) { return list.binarySearch( value ); }
    @Override
	public int binarySearch( float value, int fromIndex, int toIndex ) {
		return list.binarySearch( value, fromIndex, toIndex );
	}

    @Override
	public int indexOf( int offset, float value )     { return list.indexOf( offset, value ); }
    @Override
	public int lastIndexOf( int offset, float value ) { return list.lastIndexOf( offset, value ); }
    @Override
	public TFloatList grep( TFloatProcedure condition ) { return list.grep( condition ); }
    @Override
	public TFloatList inverseGrep( TFloatProcedure condition ) { return list.inverseGrep( condition ); }

    @Override
	public float max()    { return list.max(); }
    @Override
	public float min()    { return list.min(); }
    @Override
	public float sum()    { return list.sum(); }

    @Override
	public TFloatList subList( int fromIndex, int toIndex ) {
		return new TUnmodifiableFloatList( list.subList( fromIndex, toIndex ) );
	}

	// TODO: Do we want to fullt implement ListIterator?
//        public TIntListIterator listIterator() 	{return listIterator(0);}
//
//        public ListIterator<E> listIterator(final int index) {
//            return new ListIterator<E>() {
//                ListIterator<? extends E> i = list.listIterator(index);
//
//                public boolean hasNext()     {return i.hasNext();}
//                public E next()		     {return i.next();}
//                public boolean hasPrevious() {return i.hasPrevious();}
//                public E previous()	     {return i.previous();}
//                public int nextIndex()       {return i.nextIndex();}
//                public int previousIndex()   {return i.previousIndex();}
//
//                public void remove() {
//                    throw new UnsupportedOperationException();
//                }
//                public void set(E e) {
//                    throw new UnsupportedOperationException();
//                }
//                public void add(E e) {
//                    throw new UnsupportedOperationException();
//                }
//            };
//        }

	/**
	 * UnmodifiableRandomAccessList instances are serialized as
	 * UnmodifiableList instances to allow them to be deserialized
	 * in pre-1.4 JREs (which do not have UnmodifiableRandomAccessList).
	 * This method inverts the transformation.  As a beneficial
	 * side-effect, it also grafts the RandomAccess marker onto
	 * UnmodifiableList instances that were serialized in pre-1.4 JREs.
	 *
	 * Note: Unfortunately, UnmodifiableRandomAccessList instances
	 * serialized in 1.4.1 and deserialized in 1.4 will become
	 * UnmodifiableList instances, as this method was missing in 1.4.
	 */
	private Object readResolve() {
		return ( list instanceof RandomAccess
		? new TUnmodifiableRandomAccessFloatList( list )
		: this);
	}

    @Override
	public void add( float[] vals ) { throw new UnsupportedOperationException(); }
    @Override
	public void add( float[] vals, int offset, int length ) { throw new UnsupportedOperationException(); }

    @Override
	public float removeAt( int offset ) { throw new UnsupportedOperationException(); }
    @Override
	public void remove( int offset, int length ) { throw new UnsupportedOperationException(); }

    @Override
	public void insert( int offset, float value ) { throw new UnsupportedOperationException(); }
    @Override
	public void insert( int offset, float[] values ) { throw new UnsupportedOperationException(); }
    @Override
	public void insert( int offset, float[] values, int valOffset, int len ) { throw new UnsupportedOperationException(); }

    @Override
	public float set( int offset, float val ) { throw new UnsupportedOperationException(); }
    @Override
	public void set( int offset, float[] values ) { throw new UnsupportedOperationException(); }
    @Override
	public void set( int offset, float[] values, int valOffset, int length ) { throw new UnsupportedOperationException(); }

    @Override
	public float replace( int offset, float val ) { throw new UnsupportedOperationException(); }

    @Override
	public void transformValues( TFloatFunction function ) { throw new UnsupportedOperationException(); }

    @Override
	public void reverse() { throw new UnsupportedOperationException(); }
    @Override
	public void reverse( int from, int to ) { throw new UnsupportedOperationException(); }
    @Override
	public void shuffle( Random rand ) { throw new UnsupportedOperationException(); }

    @Override
	public void sort() { throw new UnsupportedOperationException(); }
    @Override
	public void sort( int fromIndex, int toIndex ) { throw new UnsupportedOperationException(); }
    @Override
	public void fill( float val ) { throw new UnsupportedOperationException(); }
    @Override
	public void fill( int fromIndex, int toIndex, float val ) { throw new UnsupportedOperationException(); }
}
