package com.systemdesignlab.urlshortener.util;

import java.util.BitSet;

import org.springframework.stereotype.Component;

@Component
public class BloomFilter {

    private static final int SIZE = 100000;

    private final BitSet bitSet = new BitSet(SIZE);

    private int hash1(String value) {
        return Math.abs(value.hashCode()) % SIZE;
    }

    private int hash2(String value) {
        return Math.abs(value.hashCode() * 31) % SIZE;
    }

    private int hash3(String value) {
        return Math.abs(value.hashCode() * 17) % SIZE;
    }

    public void add(String value) {
        bitSet.set(hash1(value));
        bitSet.set(hash2(value));
        bitSet.set(hash3(value));
    }

    public boolean mightContain(String value) {

        return bitSet.get(hash1(value))
                && bitSet.get(hash2(value))
                && bitSet.get(hash3(value));
    }
    
}