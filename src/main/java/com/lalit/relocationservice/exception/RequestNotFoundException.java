package com.lalit.relocationservice.exception;

public class RequestNotFoundException extends RuntimeException{
    public RequestNotFoundException(String message){
        super(message);
    }
}