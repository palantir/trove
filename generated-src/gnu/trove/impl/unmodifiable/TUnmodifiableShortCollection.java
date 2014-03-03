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


public class TUnmodifiableShortCollection implements TShortCollection, Serializable {
	private static final long serialVersionUID = 1820017752578914078L;

	final TShortCollection c;

	public TUnmodifiableShortCollection( TShortCollection c ) {
		if ( c == null )
			throw new NullPointerException();
		this.c = c;
	}

    @Override
	public int size()                   { return c.size(); }
    @Override
	public boolean isEmpty() 	        { return c.isEmpty(); }
    @Override
	public boolean contains( short o )    { return c.contains( o ); }
    @Override
	public short[] toArray()              { return c.toArray(); }
    @Override
	public short[] toArray( short[] a )     { return c.toArray( a ); }
    @Override
	public String toString()            { return c.toString(); }
    @Override
	public short getNoEntryValue()        { return c.getNoEntryValue(); }
    @Override
	public boolean forEach( TShortProcedure procedure ) { return c.forEach( procedure ); }

    @Override
	public TShortIterator iterator() {
		return new TShortIterator() {
			TShortIterator i = c.iterator();

            @Override
			public boolean hasNext()    { return i.hasNext(); }
            @Override
			public short next()           { return i.next(); }
            @Override
			public void remove()        { throw new UnsupportedOperationException(); }
		};
	}

    @Override
	public boolean add( short e ) { throw new UnsupportedOperationException(); }
    @Override
	public boolean remove( short o ) { throw new UnsupportedOperationException(); }

    @Override
	public boolean containsAll( Collection<?> coll ) { return c.containsAll( coll ); }
    @Override
	public boolean containsAll( TShortCollection coll ) { return c.containsAll( coll ); }
    @Override
	public boolean containsAll( short[] array ) { return c.containsAll( array ); }

    @Override
	public boolean addAll( TShortCollection coll ) { throw new UnsupportedOperationException(); }
    @Override
	public boolean addAll( Collection<? extends Short> coll ) { throw new UnsupportedOperationException(); }
    @Override
	public boolean addAll( short[] array ) { throw new UnsupportedOperationException(); }

    @Override
	public boolean removeAll( Collection<?> coll ) { throw new UnsupportedOperationException(); }
    @Override
	public boolean removeAll( TShortCollection coll ) { throw new UnsupportedOperationException(); }
    @Override
	public boolean removeAll( short[] array ) { throw new UnsupportedOperationException(); }

    @Override
	public boolean retainAll( Collection<?> coll ) { throw new UnsupportedOperationException(); }
    @Override
	public boolean retainAll( TShortCollection coll ) { throw new UnsupportedOperationException(); }
    @Override
	public boolean retainAll( short[] array ) { throw new UnsupportedOperationException(); }

    @Override
	public void clear() { throw new UnsupportedOperationException(); }
}
