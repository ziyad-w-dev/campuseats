package com.campuseats.exception;

// Rails equivalent: ActiveRecord::RecordNotFound (Rails turns it into a 404)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
