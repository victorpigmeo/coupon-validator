package dev.pigmeo.couponvalidator.application.mappers;

import dev.pigmeo.couponvalidator.application.dto.coupon.CouponRedeemRequest;
import dev.pigmeo.couponvalidator.application.dto.coupon.CouponRedeemResponse;
import dev.pigmeo.couponvalidator.domain.entities.coupon.CouponRedemption;
import dev.pigmeo.couponvalidator.domain.models.coupon.CouponRedeemCommand;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public CouponRedeemResponse toRedeemResponse(CouponRedemption couponRedemption) {
        return new CouponRedeemResponse(
                couponRedemption.getOriginalAmount(),
                couponRedemption.getDiscountedAmount(),
                couponRedemption.getDiscountPercentage(),
                couponRedemption.getCampaign().getRedemptionCount()
        );
    }

    public CouponRedeemCommand toRedeemCouponCommand(CouponRedeemRequest couponRedeemRequest) {
        return new CouponRedeemCommand(
                couponRedeemRequest.tier(),
                couponRedeemRequest.amount(),
                couponRedeemRequest.couponCode(),
                couponRedeemRequest.customerId()
        );
    }
}
