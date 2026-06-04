package dev.pigmeo.couponvalidator.domain.exceptions;

public class MaxRedemptionsPerCustomerExccededException extends RuntimeException{
    public MaxRedemptionsPerCustomerExccededException(String message){
        super(message);
    }
}
