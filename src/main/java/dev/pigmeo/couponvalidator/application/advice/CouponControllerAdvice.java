package dev.pigmeo.couponvalidator.application.advice;

import dev.pigmeo.couponvalidator.application.dto.ErrorResponse;
import dev.pigmeo.couponvalidator.domain.exceptions.CampaignDatesOutOfBoundsException;
import dev.pigmeo.couponvalidator.domain.exceptions.CampaignNotFoundException;
import dev.pigmeo.couponvalidator.domain.exceptions.CouponsOnlyOnPaidPlansException;
import dev.pigmeo.couponvalidator.domain.exceptions.MaxRedemptionsPerCustomerExceededException;
import dev.pigmeo.couponvalidator.domain.exceptions.MaxRedemptionsReachedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CouponControllerAdvice {

    @ExceptionHandler(CouponsOnlyOnPaidPlansException.class)
    public ResponseEntity<ErrorResponse> handle(CouponsOnlyOnPaidPlansException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CampaignNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MaxRedemptionsReachedException.class)
    public ResponseEntity<ErrorResponse> handle(MaxRedemptionsReachedException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MaxRedemptionsPerCustomerExceededException.class)
    public ResponseEntity<ErrorResponse> handle(MaxRedemptionsPerCustomerExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CampaignDatesOutOfBoundsException.class)
    public ResponseEntity<ErrorResponse> handle(CampaignDatesOutOfBoundsException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }
}
