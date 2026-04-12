package com.lalit.relocationservice.exception;

public class ProviderNotFoundException extends RuntimeException{
    public ProviderNotFoundException(String message){
        super(message);
    }
}