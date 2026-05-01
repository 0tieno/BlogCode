package com.kampuni.blogging_platform_api.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException (String message){
        super(message);
    }
}
