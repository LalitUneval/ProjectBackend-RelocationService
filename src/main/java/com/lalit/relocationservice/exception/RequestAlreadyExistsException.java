package com.lalit.relocationservice.exception;

public class RequestAlreadyExistsException  extends RuntimeException{
    public RequestAlreadyExistsException (String message)
    {
        super(message);
    }
}
