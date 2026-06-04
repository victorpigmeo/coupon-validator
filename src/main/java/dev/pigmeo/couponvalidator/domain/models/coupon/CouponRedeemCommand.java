package dev.pigmeo.couponvalidator.domain.models.coupon;

import dev.pigmeo.couponvalidator.domain.models.subscription.Tier;

import java.math.BigDecimal;

public record CouponRedeemCommand(
        Tier tier,
        BigDecimal amount,
        String couponCode,
        Long customerId
) {
}
