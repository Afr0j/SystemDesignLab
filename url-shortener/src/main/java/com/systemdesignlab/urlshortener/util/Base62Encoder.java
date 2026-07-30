package com.systemdesignlab.urlshortener.util;

public final class Base62Encoder {

    private static final char[] BASE62 =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                    .toCharArray();

    private Base62Encoder() {
    }

    public static String encode(long id) {
    	if (id < 0) {
    	    throw new IllegalArgumentException("ID cannot be negative");
    	}

        if (id == 0) {
            return "0";
        }

        StringBuilder encoded = new StringBuilder();

        while (id > 0) {
            encoded.append(BASE62[(int) (id % BASE62.length)]);
            id /= BASE62.length;
        }

      return encoded.reverse().toString();
    }
}