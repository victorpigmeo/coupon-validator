package dev.pigmeo.couponvalidator.domain.exceptions;

public class MaxRedemptionsReachedException extends RuntimeException{
    public MaxRedemptionsReachedException(String message){
        super(message);
    }
}
