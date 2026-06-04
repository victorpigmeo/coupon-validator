package dev.pigmeo.couponvalidator.domain.services;

import dev.pigmeo.couponvalidator.domain.entities.campaign.Campaign;
import dev.pigmeo.couponvalidator.domain.entities.coupon.CouponRedemption;
import dev.pigmeo.couponvalidator.domain.exceptions.CampaignNotFoundException;
import dev.pigmeo.couponvalidator.domain.models.coupon.CouponRedeemCommand;
import dev.pigmeo.couponvalidator.infrastructure.repositories.campaign.CampaignRepository;
import dev.pigmeo.couponvalidator.infrastructure.repositories.coupon.CouponRedemptionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsyncCouponService {
    private final CampaignRepository campaignRepository;
    private final CampaignCacheService campaignCacheService;
    private final CouponRedemptionRepository couponRedemptionRepository;

    AsyncCouponService(
            CampaignRepository campaignRepository,
            CampaignCacheService campaignCacheService,
            CouponRedemptionRepository couponRedemptionRepository
    ) {
        this.campaignRepository = campaignRepository;
        this.campaignCacheService = campaignCacheService;
        this.couponRedemptionRepository = couponRedemptionRepository;
    }

    @Async
    @Transactional
    public void createCouponRedemption(CouponRedeemCommand couponRedeemCommand) {
        campaignRepository.incrementCount(couponRedeemCommand.couponCode());
        Campaign campaign = this.campaignRepository.findByCouponCode(couponRedeemCommand.couponCode())
                .orElseThrow(() -> new CampaignNotFoundException("Campaign not found"));

        this.couponRedemptionRepository.save(new CouponRedemption(
                couponRedeemCommand.customerId(),
                couponRedeemCommand.amount(),
                campaign.getDiscountPercentage(),
                campaign
        ));

        this.campaignCacheService.updateCachedRedemptionCount(campaign);
    }

}
