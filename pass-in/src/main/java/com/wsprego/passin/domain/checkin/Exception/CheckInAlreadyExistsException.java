package com.wsprego.passin.domain.checkin.Exception;

public class CheckInAlreadyExistsException extends RuntimeException{
    public CheckInAlreadyExistsException(String message){
        super(message);
    }
}
