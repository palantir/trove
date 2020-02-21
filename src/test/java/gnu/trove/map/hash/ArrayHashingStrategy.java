package gnu.trove.map.hash;

import java.io.Serializable;
import java.util.Arrays;

import gnu.trove.strategy.HashingStrategy;


/**
*
*/
public class ArrayHashingStrategy
    implements HashingStrategy<char[]>, Serializable {

    @Override
    public int computeHashCode( char[] o ) {
        return Arrays.hashCode( o );
    }

    @Override
    public boolean equals( char[] o1, char[] o2 ) {
        return Arrays.equals( o1, o2 );
    }
}
