package gnu.trove.array;

import java.util.Arrays;

//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

/**
 * Direct memory allocated array.
 * <p>
 * Uses {@link sun.misc.Unsafe} directly to avoid byte
 * swaps due to endian mis-matches that can result when using
 * {@link ByteBuffer.allocateDirect()}
 */
public class TCharOffheapArray extends AbstractOffheapArray {
    private static final int SHIFT = 31 - Integer.numberOfLeadingZeros(2);
    private static final long ARRAY_OFFSET = UNSAFE.arrayBaseOffset(char[].class);
    private static final int ARRAY_SHIFT = 31 - Integer.numberOfLeadingZeros(UNSAFE.arrayIndexScale(char[].class));
    private static final long UNSAFE_COPY_THRESHOLD = 1024L * 1024L;

    public TCharOffheapArray(long capacity) {
        super(capacity << SHIFT);
    }

    @Override
    public void resize(long newCapacity) {
        super.resize(newCapacity << SHIFT);
    }

    public void put(long index, char value) {
        check(index);
        UNSAFE.putChar(address + (index << SHIFT), value);
    }

    public char get(long index) {
        check(index);
        return UNSAFE.getChar(address + (index << SHIFT));
    }

    public void toArray(long srcIndex, char[] dst, int dstIndex, int length) {
        check(srcIndex, dstIndex, dst.length, length);
        long srcAddress = address + (srcIndex << SHIFT);
        long dstOffset = ARRAY_OFFSET + (((long) dstIndex) << ARRAY_SHIFT);
        long longLength = ((long) length) << SHIFT;
        while (longLength > 0) {
            long size = (length > UNSAFE_COPY_THRESHOLD) ? UNSAFE_COPY_THRESHOLD : longLength;
            UNSAFE.copyMemory(null, srcAddress, dst, dstOffset, size);
            longLength -= size;
            srcAddress += size;
            dstOffset += size;
        }
    }

    public void fromArray(char[] src, int srcIndex, long dstIndex, int length) {
        check(dstIndex, srcIndex, src.length, length);
        long dstAddress = address + (dstIndex << SHIFT);
        long srcOffset = ARRAY_OFFSET + (((long) srcIndex) << ARRAY_SHIFT);
        long longLength = ((long) length) << SHIFT;
        while (longLength > 0) {
            long size = (length > UNSAFE_COPY_THRESHOLD) ? UNSAFE_COPY_THRESHOLD : longLength;
            UNSAFE.copyMemory(src, srcOffset, null, dstAddress, size);
            longLength -= size;
            dstAddress += size;
            srcOffset += size;
        }
    }

    @Override
    public long capacity() {
        return capacity >> SHIFT;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (long i = 0; i < capacity(); i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(UNSAFE.getChar(address + (i << SHIFT)));
        }
        s.append("]");
        return s.toString();
    }

    // Simple runtime checks to help catch any "surprising" changes in the Unsafe api.
    static {
        TCharOffheapArray array = new TCharOffheapArray(10);
        assert(array.capacity() == 10);
        array.put(3, (char) -45);
        array.put(5, (char) 121);
        staticAssert(array.get(1) == 0);
        staticAssert(array.get(2) == 0);
        staticAssert(array.get(3) == -45);
        staticAssert(array.get(4) == 0);
        staticAssert(array.get(5) == 121);
        staticAssert(array.get(6) == 0);
        staticAssert(array.get(7) == 0);
        staticAssert(array.get(8) == 0);
        array.resize(5);
        staticAssert(array.capacity() == 5);
        staticAssert(array.get(1) == 0);
        staticAssert(array.get(2) == 0);
        staticAssert(array.get(3) == -45);
        staticAssert(array.get(4) == 0);
        array.resize(20);
        staticAssert(array.capacity() == 20);
        staticAssert(array.get(1) == 0);
        staticAssert(array.get(2) == 0);
        staticAssert(array.get(3) == -45);
        staticAssert(array.get(4) == 0);
        array.fromArray(new char[] {(char) -3, 0, 4, 5}, 0, 7, 4);
        staticAssert(array.get(6) == 0);
        staticAssert(array.get(7) == -3);
        staticAssert(array.get(8) == 0);
        staticAssert(array.get(9) == 4);
        staticAssert(array.get(10) == 5);
        staticAssert(array.get(11) == 0);
        char[] dst = new char[5];
        array.toArray(3, dst, 0, 5);
        staticAssert(Arrays.equals(dst, new char[] {(char) -45, 0, 0, 0, (char) -3}));
        array.clear();
        array.toArray(3, dst, 0, 5);
        staticAssert(Arrays.equals(dst, new char[] {0, 0, 0, 0, 0}));
        array.free();
    }

    private static void staticAssert(boolean condition) {
        if (!condition) {
            throw new Error("Failed initialization checks");
        }
    }
}
