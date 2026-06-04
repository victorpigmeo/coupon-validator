package dev.pigmeo.couponvalidator.domain.exceptions;

public class CampaignDatesOutOfBoundsException extends RuntimeException{
    public CampaignDatesOutOfBoundsException(String message){
        super(message);
    }
}
