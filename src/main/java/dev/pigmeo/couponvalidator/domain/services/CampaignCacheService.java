package dev.pigmeo.couponvalidator.domain.services;

import dev.pigmeo.couponvalidator.domain.entities.campaign.Campaign;
import dev.pigmeo.couponvalidator.domain.exceptions.CampaignNotFoundException;
import dev.pigmeo.couponvalidator.infrastructure.repositories.campaign.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.Lock;

@Service
public class CampaignCacheService {

    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

    private final static String CAMPAIGN_CACHE_LOCK_KEY = "campaign-write:";
    private final static String CAMPAIGNS_CACHE_KEY_PREFIX = "campaigns::";
    private final static String CAMPAIGN_COUNTER_CACHE_KEY_PREFIX = "campaigns:redemptionCount:";
    private final static String CAMPAIGN_PER_USER_COUNTER_CACHE_KEY_PREFIX =  "campaigns:customerRedemptionCount:";

    private final CampaignRepository campaignRepository;
    private final LockRegistry lockRegistry;
    private final RedisTemplate<String, Object> redisTemplate;

    public CampaignCacheService(
            CampaignRepository campaignRepository,
            LockRegistry lockRegistry,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.campaignRepository = campaignRepository;
        this.lockRegistry = lockRegistry;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    @Cacheable(cacheNames = "campaigns", key = "#couponCode")
    public Campaign findByCouponCode(String couponCode) {
        logger.warn("Cache miss for campaign: [couponCode: {}]", couponCode);
        Campaign campaign = this.campaignRepository.findByCouponCodeWithLock(couponCode)
                .orElseThrow(() -> new CampaignNotFoundException("Campaign not found"));

        redisTemplate.opsForValue()
                .set(CAMPAIGN_COUNTER_CACHE_KEY_PREFIX + campaign.getCouponCode(), campaign.getRedemptionCount());
        return campaign;
    }

    public void updateCachedRedemptionCount(Campaign campaign) {
        Lock lock = lockRegistry.obtain(CAMPAIGN_CACHE_LOCK_KEY + campaign.getCouponCode());
        lock.lock();
        try {
            this.redisTemplate.opsForValue()
                    .set(CAMPAIGNS_CACHE_KEY_PREFIX + campaign.getCouponCode(), campaign);
        } finally {
            lock.unlock();
        }
    }

    public Long incrementCacheCampaignCounter(Campaign campaign) {
        final String campaignCounterRedisKey = CAMPAIGN_COUNTER_CACHE_KEY_PREFIX + campaign.getCouponCode();

        return this.redisTemplate.opsForValue().increment(campaignCounterRedisKey);
    }

    public Long incrementCacheCustomerCounter(Long customerId, String couponCode) {
        final String customerCounterRedisKey =
                CAMPAIGN_PER_USER_COUNTER_CACHE_KEY_PREFIX + ":" + couponCode + ":" + customerId;

        return this.redisTemplate.opsForValue().increment(customerCounterRedisKey);
    }

    public void decrementCacheCampaignCounter(Campaign campaign) {
        final String campaignCounterRedisKey = CAMPAIGN_COUNTER_CACHE_KEY_PREFIX + campaign.getCouponCode();

        this.redisTemplate.opsForValue().decrement(campaignCounterRedisKey);
    }

    public void decrementCacheCustomerCounter(Long customerId) {
        final String customerCounterRedisKey = CAMPAIGN_PER_USER_COUNTER_CACHE_KEY_PREFIX + customerId;

        this.redisTemplate.opsForValue().decrement(customerCounterRedisKey);
    }
}
