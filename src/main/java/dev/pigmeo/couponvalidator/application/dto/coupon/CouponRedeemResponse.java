package dev.pigmeo.couponvalidator.application.dto.coupon;

import java.math.BigDecimal;

public record CouponRedeemResponse(
        BigDecimal originalAmount,
        BigDecimal discountedAmount,
        short discountPercentage,
        int redemptions
) { }
