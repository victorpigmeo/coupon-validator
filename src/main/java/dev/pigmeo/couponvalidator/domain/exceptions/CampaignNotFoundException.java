package dev.pigmeo.couponvalidator.domain.exceptions;

public class CampaignNotFoundException extends RuntimeException{
    public CampaignNotFoundException(String message){
        super(message);
    }
}
