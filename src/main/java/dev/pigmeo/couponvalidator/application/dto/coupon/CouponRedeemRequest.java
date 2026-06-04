package dev.pigmeo.couponvalidator.application.dto.coupon;

import dev.pigmeo.couponvalidator.domain.models.subscription.Tier;

import java.math.BigDecimal;

public record CouponRedeemRequest(
        Tier tier,
        BigDecimal amount,
        String couponCode,
        Long customerId
) { }