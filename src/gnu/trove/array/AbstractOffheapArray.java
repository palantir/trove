package gnu.trove.array;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicLong;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

/**
 * Direct memory allocated byte buffer.
 * <p>
 * Uses {@link sun.misc.Unsafe} directly to avoid byte
 * swaps due to endian mis-matches that can result when using
 * {@link ByteBuffer.allocateDirect()}
 */
public abstract class AbstractOffheapArray {
    protected static final Unsafe UNSAFE;

    private static class Deallocator implements Runnable {

        private final AtomicLong boxedAddress;

        private Deallocator(AtomicLong boxedAddress) {
            if (boxedAddress.get() == 0) {
                throw new IllegalArgumentException("Cannot allocate with address 0");
            }
            this.boxedAddress = boxedAddress;
        }

        @Override
        public void run() {
            UNSAFE.freeMemory(boxedAddress.getAndSet(0));
        }
    }

    /**
     * A cleaner is basically a fancy finalizer with nicer semantics. This cleaner will
     * deallocate the manually allocated memory used by this offheap array after the
     * offheap array becomes unreachable.
     * <p>
     * http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/sun/misc/Cleaner.java
     */
    private final Cleaner cleaner;
    private final AtomicLong boxedAddress;
    protected long address;
    protected long capacity;

    public AbstractOffheapArray(long capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Cannot allocate with capacity=" + capacity);
        }
        address = UNSAFE.allocateMemory(capacity);
        boxedAddress = new AtomicLong(address);
        cleaner = Cleaner.create(this, new Deallocator(boxedAddress));
        UNSAFE.setMemory(address, capacity, (byte) 0);
        this.capacity = capacity;
    }

    protected void resize(long newCapacity) {
        long currAddress = address;
        if (newCapacity < 0) {
            throw new IllegalArgumentException("Cannot reallocate with capacity=" + newCapacity);
        }
        if (currAddress == 0 || boxedAddress.get() != currAddress) {
            throw new IllegalStateException("Cannot reallocate after freeing");
        }
        long newAddress = UNSAFE.reallocateMemory(currAddress, newCapacity);
        if (!boxedAddress.compareAndSet(currAddress, newAddress)) {
            UNSAFE.freeMemory(newAddress);
            throw new IllegalStateException("Race condition while resizing");
        }
        address = boxedAddress.get();
        if (newCapacity > capacity) {
            UNSAFE.setMemory(address + capacity, newCapacity - capacity, (byte) 0);
        }
        this.capacity = newCapacity;
    }

    public void clear() {
        if (capacity > 0) {
            UNSAFE.setMemory(address, capacity, (byte) 0);
        }
    }

    public void free() {
        address = 0;
        capacity = 0;

        // Cleaner is guaranteed to run its runnable at most once, so its fine to
        // call free multiple times, and nothing additional will happen when this
        // array becomes unreachable.
        cleaner.clean();
    }

    public abstract long capacity();

    protected void check(long index) {
        if (index < 0 || index >= capacity()) {
            throw new IndexOutOfBoundsException("index=" + index + ", capacity=" + capacity());
        }
    }

    protected void check(long ourIndex, int arrayIndex, int arrayLength, int length) {
        if (ourIndex < 0 || length < 0 || ourIndex + length > capacity() ||
                arrayIndex < 0 || arrayIndex + length > arrayLength) {
            throw new IndexOutOfBoundsException("ourIndex=" + ourIndex + ", ourLength=" + capacity() +
                    ", arrayIndex=" + arrayIndex + ", arrayLength=" + arrayLength + ", length=" + length);
        }
    }

    static {
        Field f = getPrivateDeclaredField(Unsafe.class, "theUnsafe");
        try {
            UNSAFE = (Unsafe)f.get(null);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets a private field from a class.
     */
    private static Field getPrivateDeclaredField(final Class<?> clazz, final String name) {
        // N.B., the setAccessible(true) call should happen within a doPrivileged() block.
        return AccessController.doPrivileged(new PrivilegedAction<Field>() {
            @Override
            public Field run() {
                try {
                    Field f = clazz.getDeclaredField(name);
                    f.setAccessible(true);
                    return f;
                } catch (SecurityException e) {
                    throw new IllegalStateException(e);
                } catch (NoSuchFieldException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }
}
