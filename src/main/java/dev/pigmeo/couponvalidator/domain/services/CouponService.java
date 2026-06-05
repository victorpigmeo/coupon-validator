package dev.pigmeo.couponvalidator.domain.services;

import dev.pigmeo.couponvalidator.domain.entities.campaign.Campaign;
import dev.pigmeo.couponvalidator.domain.entities.coupon.CouponRedemption;
import dev.pigmeo.couponvalidator.domain.exceptions.CampaignDatesOutOfBoundsException;
import dev.pigmeo.couponvalidator.domain.exceptions.CouponsOnlyOnPaidPlansException;
import dev.pigmeo.couponvalidator.domain.exceptions.MaxRedemptionsPerCustomerExceededException;
import dev.pigmeo.couponvalidator.domain.exceptions.MaxRedemptionsReachedException;
import dev.pigmeo.couponvalidator.domain.models.coupon.CouponRedeemCommand;
import dev.pigmeo.couponvalidator.domain.models.subscription.Tier;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponService {

    private final CampaignCacheService campaignCacheService;
    private final AsyncCouponService asyncCouponService;

    public CouponService(
            CampaignCacheService campaignCacheService,
            AsyncCouponService asyncCouponService
    ) {
        this.campaignCacheService = campaignCacheService;
        this.asyncCouponService = asyncCouponService;
    }

    public CouponRedemption redeemCoupon(CouponRedeemCommand couponRedeemCommand) {
        if (!couponRedeemCommand.tier().equals(Tier.PAID)) {
            throw new CouponsOnlyOnPaidPlansException("Coupons are only for paid plans");
        }

        final Instant now = Instant.now();
        Campaign campaignCachedMetadata =
                this.campaignCacheService.findByCouponCode(couponRedeemCommand.couponCode());

        if (now.isBefore(campaignCachedMetadata.getStartDate()) || now.isAfter(campaignCachedMetadata.getEndDate())) {
            throw new CampaignDatesOutOfBoundsException("This campaign didn't started yet, or is already over");
        }

        final Long newCampaignCount = this.campaignCacheService.incrementCacheCampaignCounter(campaignCachedMetadata);

        if (newCampaignCount != null && newCampaignCount > campaignCachedMetadata.getMaxRedemptions()) {
            this.campaignCacheService.decrementCacheCampaignCounter(campaignCachedMetadata);
            throw new MaxRedemptionsReachedException("This campaign reached its maximum redemption");
        }

        final Long newCustomerCount =
                this.campaignCacheService.incrementCacheCustomerCounter(
                        couponRedeemCommand.customerId(), campaignCachedMetadata.getCouponCode());

        if (newCustomerCount > campaignCachedMetadata.getPerCustomerRedemptions()) {
            this.campaignCacheService.decrementCacheCustomerCounter(couponRedeemCommand.customerId());
            throw new MaxRedemptionsPerCustomerExceededException("This customer already use all his coupons");
        }

        this.asyncCouponService.createCouponRedemption(couponRedeemCommand);
        return new CouponRedemption(
                couponRedeemCommand.customerId(),
                couponRedeemCommand.amount(),
                campaignCachedMetadata.getDiscountPercentage(),
                campaignCachedMetadata
        );
    }

}
