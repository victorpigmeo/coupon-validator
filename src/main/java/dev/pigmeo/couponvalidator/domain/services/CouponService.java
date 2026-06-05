package dev.pigmeo.couponvalidator.domain.services;

import dev.pigmeo.couponvalidator.domain.entities.campaign.Campaign;
import dev.pigmeo.couponvalidator.domain.entities.coupon.CouponRedemption;
import dev.pigmeo.couponvalidator.domain.exceptions.CampaignDatesOutOfBoundsException;
import dev.pigmeo.couponvalidator.domain.exceptions.CouponsOnlyOnPaidPlansException;
import dev.pigmeo.couponvalidator.domain.exceptions.MaxRedemptionsPerCustomerExceededException;
import dev.pigmeo.couponvalidator.domain.exceptions.MaxRedemptionsReachedException;
import dev.pigmeo.couponvalidator.domain.models.coupon.CouponRedeemCommand;
import dev.pigmeo.couponvalidator.domain.models.subscription.Tier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CouponService {
    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

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
        logger.info("Validating coupon: {}", couponRedeemCommand.couponCode());

        if (!couponRedeemCommand.tier().equals(Tier.PAID)) {
            logger.error("Coupons are only for paid plans");
            throw new CouponsOnlyOnPaidPlansException("Coupons are only for paid plans");
        }

        final Instant now = Instant.now();
        Campaign campaignCachedMetadata =
                this.campaignCacheService.findByCouponCode(couponRedeemCommand.couponCode());

        if (now.isBefore(campaignCachedMetadata.getStartDate()) || now.isAfter(campaignCachedMetadata.getEndDate())) {
            logger.error("Outside campaign start/end date: [couponCode: {}]", couponRedeemCommand.couponCode());
            throw new CampaignDatesOutOfBoundsException("This campaign didn't started yet, or is already over");
        }

        final Long newCustomerCount =
                this.campaignCacheService.incrementCacheCustomerCounter(
                        couponRedeemCommand.customerId(), campaignCachedMetadata.getCouponCode());

        if (newCustomerCount > campaignCachedMetadata.getPerCustomerRedemptions()) {
            logger.error("Customer exceeded redemption quota: [couponCode: {}, customer: {}]",
                    couponRedeemCommand.couponCode(), couponRedeemCommand.customerId());
            this.campaignCacheService.decrementCacheCustomerCounter(couponRedeemCommand.customerId());
            throw new MaxRedemptionsPerCustomerExceededException("This customer already use all his coupons");
        }

        final Long newCampaignCount = this.campaignCacheService.incrementCacheCampaignCounter(campaignCachedMetadata);

        if (newCampaignCount != null && newCampaignCount > campaignCachedMetadata.getMaxRedemptions()) {
            logger.error("Campaign exceeded redemption quota: [couponCode: {}]", couponRedeemCommand.couponCode());
            this.campaignCacheService.decrementCacheCampaignCounter(campaignCachedMetadata);
            throw new MaxRedemptionsReachedException("This campaign reached its maximum redemption");
        }

        this.asyncCouponService.createCouponRedemption(couponRedeemCommand);

        logger.info("Coupon redeemed: [couponCode: {}, customerId: {}]",
                couponRedeemCommand.couponCode(), couponRedeemCommand.customerId());
        return new CouponRedemption(
                couponRedeemCommand.customerId(),
                couponRedeemCommand.amount(),
                campaignCachedMetadata.getDiscountPercentage(),
                campaignCachedMetadata
        );
    }

}
