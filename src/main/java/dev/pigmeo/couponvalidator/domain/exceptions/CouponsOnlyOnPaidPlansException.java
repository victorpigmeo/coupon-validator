package dev.pigmeo.couponvalidator.domain.exceptions;

public class CouponsOnlyOnPaidPlansException extends RuntimeException {
    public CouponsOnlyOnPaidPlansException(String message) {
        super(message);
    }
}
