package com.lalit.relocationservice.exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message)
    {
        super(message);
    }
}
