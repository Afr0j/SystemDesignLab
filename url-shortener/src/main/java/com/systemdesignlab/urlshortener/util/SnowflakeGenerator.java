package com.systemdesignlab.urlshortener.util;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeGenerator {

    private static final long CUSTOM_EPOCH = 1735689600000L; // 1 Jan 2025 UTC

    private static final long MACHINE_ID_BITS = 10;
    private static final long SEQUENCE_BITS = 12;

    private static final long MAX_MACHINE_ID =
            (1L << MACHINE_ID_BITS) - 1;

    private static final long MAX_SEQUENCE =
            (1L << SEQUENCE_BITS) - 1;

    private final long machineId;

    private long sequence = 0;

    private long lastTimestamp = -1;

    public SnowflakeGenerator() {
        this(1);
    }

    public SnowflakeGenerator(long machineId) {

        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException(
                    "Machine ID must be between 0 and "
                            + MAX_MACHINE_ID);
        }

        this.machineId = machineId;
    }
    
    public synchronized long nextId() {

        long timestamp = currentTimestamp();

        if (timestamp < lastTimestamp) {
            timestamp = waitUntilNextMillis(lastTimestamp);
        }

        if (timestamp == lastTimestamp) {

            sequence = (sequence + 1) & MAX_SEQUENCE;

            if (sequence == 0) {
                timestamp = waitUntilNextMillis(lastTimestamp);
            }

        } else {

            sequence = 0;

        }

        lastTimestamp = timestamp;

        long elapsedTime = timestamp - CUSTOM_EPOCH;

        return (elapsedTime << (MACHINE_ID_BITS + SEQUENCE_BITS))
                | (machineId << SEQUENCE_BITS)
                | sequence;
    }
    private long currentTimestamp() {
        return System.currentTimeMillis();
    }
    private long waitUntilNextMillis(long lastTimestamp) {

        long timestamp = currentTimestamp();

        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp();
        }

        return timestamp;
    }

}
