package dev.pigmeo.couponvalidator.domain.exceptions;

public class MaxRedemptionsPerCustomerExceededException extends RuntimeException{
    public MaxRedemptionsPerCustomerExceededException(String message){
        super(message);
    }
}
