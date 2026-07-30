package com.systemdesignlab.urlshortener.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Base62EncoderTest {

    @Test
    void shouldEncodeZero() {
        assertEquals("0", Base62Encoder.encode(0));
    }

    @Test
    void shouldEncodeOne() {
        assertEquals("1", Base62Encoder.encode(1));
    }

    @Test
    void shouldEncodeNine() {
        assertEquals("9", Base62Encoder.encode(9));
    }

    @Test
    void shouldEncodeTen() {
        assertEquals("A", Base62Encoder.encode(10));
    }

    @Test
    void shouldEncodeThirtyFive() {
        assertEquals("Z", Base62Encoder.encode(35));
    }

    @Test
    void shouldEncodeThirtySix() {
        assertEquals("a", Base62Encoder.encode(36));
    }

    @Test
    void shouldEncodeSixtyOne() {
        assertEquals("z", Base62Encoder.encode(61));
    }

    @Test
    void shouldEncodeSixtyTwo() {
        assertEquals("10", Base62Encoder.encode(62));
    }

    @Test
    void shouldEncodeSixtyThree() {
        assertEquals("11", Base62Encoder.encode(63));
    }

    @Test
    void shouldEncodeThreeThousandEightHundredFortyFour() {
        assertEquals("100", Base62Encoder.encode(3844));
    }

    @Test
    void shouldThrowExceptionForNegativeId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Base62Encoder.encode(-1)
        );
    }
}