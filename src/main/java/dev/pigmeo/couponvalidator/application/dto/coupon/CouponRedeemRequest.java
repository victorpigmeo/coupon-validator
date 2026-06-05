package dev.pigmeo.couponvalidator.application.dto.coupon;

import dev.pigmeo.couponvalidator.domain.models.subscription.Tier;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CouponRedeemRequest(
        @NotNull Tier tier,
        @NotNull BigDecimal amount,
        @NotEmpty @Size(min = 1, max = 10) String couponCode,
        @NotNull Long customerId
) { }