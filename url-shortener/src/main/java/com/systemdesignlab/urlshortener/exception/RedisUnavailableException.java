package com.systemdesignlab.urlshortener.exception;

public class RedisUnavailableException extends RuntimeException {

public RedisUnavailableException(Throwable cause) {
super(cause);
}
}