package io.github.opencubicchunks.cubicchunks.utils;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.IntConsumer;

public class AtomicBitStorage {
    private static final int[] MAGIC = new int[]{-1, -1, 0, -2147483648, 0, 0, 1431655765, 1431655765, 0, -2147483648, 0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756, 0, -2147483648, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0, 390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378, 306783378, 0, 286331153, 286331153, 0, -2147483648, 0, 3, 252645135, 252645135, 0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0, 204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970, 178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862, 0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0, 138547332, 138547332, 0, -2147483648, 0, 4, 130150524, 130150524, 0, 126322567, 126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0, 104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, -2147483648, 0, 5};
    private final AtomicLongArray data;
    private final int bits;
    private final long mask;
    private final int size;
    private final int valuesPerLong;
    private final int divideMul;
    private final int divideAdd;
    private final int divideShift;

    public AtomicBitStorage(int i, int j) {
        this(i, j, null);
    }

    public AtomicBitStorage(int i, int j, @Nullable long[] ls) {
        Validate.inclusiveBetween(1L, 32L, i);
        this.size = j;
        this.bits = i;
        this.mask = (1L << i) - 1L;
        this.valuesPerLong = (char)(64 / i);
        int k = 3 * (this.valuesPerLong - 1);
        this.divideMul = MAGIC[k + 0];
        this.divideAdd = MAGIC[k + 1];
        this.divideShift = MAGIC[k + 2];
        int l = (j + this.valuesPerLong - 1) / this.valuesPerLong;
        if (ls != null) {
            if (ls.length != l) {
                throw new RuntimeException("Invalid length given for storage, got: " + ls.length + " but expected: " + l);
            }
            this.data = new AtomicLongArray(ls);
        } else {
            this.data = new AtomicLongArray(l);
        }

    }

    private int cellIndex(int i) {
        long l = Integer.toUnsignedLong(this.divideMul);
        long m = Integer.toUnsignedLong(this.divideAdd);
        return (int)((long)i * l + m >> 32 >> this.divideShift);
    }

    public int getAndSet(int i, int j) {
        Validate.inclusiveBetween(0L, this.size - 1, i);
        Validate.inclusiveBetween(0L, this.mask, j);
        int k = this.cellIndex(i);
        long l = this.data.get(k);
        int m = (i - k * this.valuesPerLong) * this.bits;
        long expectedValue = data.get(k);
        this.data.compareAndSet(k, expectedValue, l & ~(this.mask << m) | ((long)j & this.mask) << m);
        return (int)(l >> m & this.mask);
    }

    public void set(int i, int j) {
        Validate.inclusiveBetween(0L, this.size - 1, i);
        Validate.inclusiveBetween(0L, this.mask, j);
        int k = this.cellIndex(i);
        long l = this.data.get(k);
        int m = (i - k * this.valuesPerLong) * this.bits;
        this.data.compareAndSet(k, data.get(k), l & ~(this.mask << m) | ((long)j & this.mask) << m);
    }

    public int get(int i) {
        Validate.inclusiveBetween(0L, this.size - 1, i);
        int j = this.cellIndex(i);
        long l = this.data.get(j);
        int k = (i - j * this.valuesPerLong) * this.bits;
        return (int)(l >> k & this.mask);
    }

    public AtomicLongArray getRaw() {
        return this.data;
    }

    public int getSize() {
        return this.size;
    }

    public void getAll(IntConsumer intConsumer) {
        int i = 0;
        int lenData = data.length();
        for(int k = 0; k < lenData; ++k) {
            long l = data.get(k);

            for(int j = 0; j < this.valuesPerLong; ++j) {
                intConsumer.accept((int)(l & this.mask));
                l >>= this.bits;
                ++i;
                if (i >= this.size) {
                    return;
                }
            }
        }
    }
}
