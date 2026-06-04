package dev.pigmeo.couponvalidator.domain.entities.coupon;

import dev.pigmeo.couponvalidator.domain.entities.campaign.Campaign;
import dev.pigmeo.couponvalidator.infrastructure.Schemas;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "coupon_redemption", schema = Schemas.COUPONVALIDATOR)
public class CouponRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "redeemed_at")
    private Instant redeemed_at;

    @Column(name = "expires_at")
    private Instant expires_at;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "redeem_confirmed")
    private boolean redeem_confirmed;

    @Column(name = "original_amount")
    private BigDecimal originalAmount;

    @Column(name = "discounted_amount")
    private BigDecimal discountedAmount;

    @Column(name = "discount_percentage")
    private short discountPercentage;

    @ManyToOne
    private Campaign campaign;

    public CouponRedemption(){}

    public CouponRedemption(
            Long customerId,
            BigDecimal originalAmount,
            short discountPercentage,
            Campaign campaign
    ){
        Instant now = Instant.now();
        this.redeemed_at = now;
        this.expires_at = now.plus(15, ChronoUnit.MINUTES);
        this.customerId = customerId;
        this.redeem_confirmed = false;
        this.originalAmount = originalAmount;
        this.discountedAmount = originalAmount
                .multiply(new BigDecimal(discountPercentage))
                .movePointLeft(2)
                .setScale(2, RoundingMode.DOWN);
        this.discountPercentage = discountPercentage;
        this.campaign = campaign;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public BigDecimal getDiscountedAmount() {
        return discountedAmount;
    }

    public short getDiscountPercentage(){
        return discountPercentage;
    }

    public Campaign getCampaign() {
        return this.campaign;
    }
}
