///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2002, Eric D. Friedman All Rights Reserved.
// Copyright (c) 2009, Robert D. Eden All Rights Reserved.
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

package gnu.trove.decorator;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.iterator.TObjectDoubleIterator;

import java.io.*;
import java.util.*;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////


/**
 * Wrapper class to make a TObjectDoubleMap conform to the <tt>java.util.Map</tt> API.
 * This class simply decorates an underlying TObjectDoubleMap and translates the Object-based
 * APIs into their Trove primitive analogs.
 * <p/>
 * Note that wrapping and unwrapping primitive values is extremely inefficient.  If
 * possible, users of this class should override the appropriate methods in this class
 * and use a table of canonical values.
 * <p/>
 * Created: Mon Sep 23 22:07:40 PDT 2002
 *
 * @author Eric D. Friedman
 * @author Robert D. Eden
 * @author Jeff Randall
 */
public class TObjectDoubleMapDecorator<K> extends AbstractMap<K, Double>
    implements Map<K, Double>, Externalizable, Cloneable {

	static final long serialVersionUID = 1L;

    /**
     * the wrapped primitive map
     */
    protected TObjectDoubleMap<K> _map;


    /**
     * FOR EXTERNALIZATION ONLY!!
     */
    public TObjectDoubleMapDecorator() {}


    /**
     * Creates a wrapper that decorates the specified primitive map.
     *
     * @param map the <tt>TObjectDoubleMap</tt> to wrap.
     */
    public TObjectDoubleMapDecorator( TObjectDoubleMap<K> map ) {
        super();
        this._map = map;
    }


    /**
     * Returns a reference to the map wrapped by this decorator.
     *
     * @return the wrapped <tt>TObjectDoubleMap</tt> instance.
     */
    public TObjectDoubleMap<K> getMap() {
        return _map;
    }


    /**
     * Inserts a key/value pair into the map.
     *
     * @param key   an <code>Object</code> value
     * @param value an <code>Double</code> value
     * @return the previous value associated with <tt>key</tt>,
     *         or Integer(0) if none was found.
     */
    @Override
    public Double put( K key, Double value ) {
        if ( value == null ) return wrapValue( _map.put( key, _map.getNoEntryValue() ) );
        return wrapValue( _map.put( key, unwrapValue( value ) ) );
    }


    /**
     * Retrieves the value for <tt>key</tt>
     *
     * @param key an <code>Object</code> value
     * @return the value of <tt>key</tt> or null if no such mapping exists.
     */
    @Override
    public Double get( Object key ) {
        double v = _map.get( key );
        // There may be a false positive since primitive maps
        // cannot return null, so we have to do an extra
        // check here.
        if ( v == _map.getNoEntryValue() ) {
            return null;
        } else {
            return wrapValue( v );
        }
    }


    /**
     * Empties the map.
     */
    @Override
    public void clear() {
        this._map.clear();
    }


    /**
     * Deletes a key/value pair from the map.
     *
     * @param key an <code>Object</code> value
     * @return the removed value, or Integer(0) if it was not found in the map
     */
    @Override
    public Double remove( Object key ) {
        double v = _map.remove( key );
        // There may be a false positive since primitive maps
        // cannot return null, so we have to do an extra
        // check here.
        if ( v == _map.getNoEntryValue() ) {
            return null;
        } else {
            return wrapValue( v );
        }
    }


    /**
     * Returns a Set view on the entries of the map.
     *
     * @return a <code>Set</code> value
     */
    @Override
    public Set<Map.Entry<K,Double>> entrySet() {
        return new AbstractSet<Map.Entry<K,Double>>() {
            @Override
            public int size() {
                return _map.size();
            }

            @Override
            public boolean isEmpty() {
                return TObjectDoubleMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains( Object o ) {
                if ( o instanceof Map.Entry ) {
                    Object k = ( ( Map.Entry ) o ).getKey();
                    Object v = ( ( Map.Entry ) o ).getValue();
                    return TObjectDoubleMapDecorator.this.containsKey( k ) &&
                            TObjectDoubleMapDecorator.this.get( k ).equals( v );
                } else {
                    return false;
                }
            }

            @Override
            public Iterator<Map.Entry<K,Double>> iterator() {
                return new Iterator<Map.Entry<K,Double>>() {
                    private final TObjectDoubleIterator<K> it = _map.iterator();

                    @Override
                    public Map.Entry<K,Double> next() {
                        it.advance();
                        final K key = it.key();
                        final Double v = wrapValue( it.value() );
                        return new Map.Entry<K,Double>() {
                            private Double val = v;

                            @Override
                            public boolean equals( Object o ) {
                                return o instanceof Map.Entry &&
                                        ( ( Map.Entry ) o ).getKey().equals( key ) &&
                                        ( ( Map.Entry ) o ).getValue().equals( val );
                            }

                            @Override
                            public K getKey() {
                                return key;
                            }

                            @Override
                            public Double getValue() {
                                return val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + val.hashCode();
                            }

                            @Override
                            public Double setValue( Double value ) {
                                val = value;
                                return put( key, value );
                            }
                        };
                    }

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public boolean add( Map.Entry<K,Double> o ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove( Object o ) {
                boolean modified = false;
                if ( contains( o ) ) {
                    //noinspection unchecked
                    K key = ( ( Map.Entry<K,Double> ) o ).getKey();
                    _map.remove( key );
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<K,Double>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TObjectDoubleMapDecorator.this.clear();
            }
        };
    }


    /**
     * Checks for the presence of <tt>val</tt> in the values of the map.
     *
     * @param val an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean containsValue( Object val ) {
        return val instanceof Double && _map.containsValue( unwrapValue( val ) );
    }


    /**
     * Checks for the present of <tt>key</tt> in the keys of the map.
     *
     * @param key an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean containsKey( Object key ) {
        return _map.containsKey( key );
    }


    /**
     * Returns the number of entries in the map.
     *
     * @return the map's size.
     */
    @Override
    public int size() {
        return this._map.size();
    }


    /**
     * Indicates whether map has any entries.
     *
     * @return true if the map is empty
     */
    @Override
    public boolean isEmpty() {
        return this._map.size() == 0;
    }

    
    /**
     * Copies the key/value mappings in <tt>map</tt> into this map.
     * Note that this will be a <b>deep</b> copy, as storage is by
     * primitive value.
     *
     * @param map a <code>Map</code> value
     */
    @Override
    public void putAll( Map<? extends K, ? extends  Double> map ) {
        Iterator<? extends Entry<? extends K,? extends Double>> it = map.entrySet().iterator();
        for ( int i = map.size(); i-- > 0; ) {
            Entry<? extends K,? extends Double> e = it.next();
            this.put( e.getKey(), e.getValue() );
        }
    }


    /**
     * Wraps a value
     *
     * @param k value in the underlying map
     * @return an Object representation of the value
     */
    protected Double wrapValue( double k ) {
        return Double.valueOf( k );
    }


    /**
     * Unwraps a value
     *
     * @param value wrapped value
     * @return an unwrapped representation of the value
     */
    protected double unwrapValue( Object value ) {
        return ( ( Double ) value ).doubleValue();
    }


    // Implements Externalizable
    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {

        // VERSION
        in.readByte();

        // MAP
        //noinspection unchecked
        _map = ( TObjectDoubleMap<K> ) in.readObject();
    }


    // Implements Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // VERSION
        out.writeByte( 0 );

        // MAP
        out.writeObject( _map );
    }

} // TObjectDoubleMapDecorator
