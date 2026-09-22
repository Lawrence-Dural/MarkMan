package com.markman.core;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Generates RFC 9562 UUIDv7 values: a 48-bit millisecond timestamp followed
 * by 74 random bits. Time-ordered, unlike UUIDv4, so B-tree primary key
 * indexes on these values don't fragment the way they would with fully
 * random UUIDs.
 */
public final class Uuid7 {

    private static final SecureRandom RANDOM = new SecureRandom();

    private Uuid7() {
    }

    public static UUID generate() {
        byte[] randomBytes = new byte[10];
        RANDOM.nextBytes(randomBytes);

        long millis = System.currentTimeMillis();

        long mostSigBits = (millis & 0xFFFFFFFFFFFFL) << 16;
        mostSigBits |= 0x7L << 12; // version 7
        int randA = ((randomBytes[0] & 0xFF) << 8 | (randomBytes[1] & 0xFF)) & 0x0FFF;
        mostSigBits |= randA;

        long randB = 0;
        for (int i = 2; i < 10; i++) {
            randB = (randB << 8) | (randomBytes[i] & 0xFF);
        }
        randB &= 0x3FFFFFFFFFFFFFFFL; // keep the low 62 bits free for the variant

        long leastSigBits = (0x2L << 62) | randB; // variant 10

        return new UUID(mostSigBits, leastSigBits);
    }
}
