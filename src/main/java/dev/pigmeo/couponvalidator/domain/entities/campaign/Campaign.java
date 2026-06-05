package dev.pigmeo.couponvalidator.domain.entities.campaign;

import dev.pigmeo.couponvalidator.infrastructure.Schemas;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "campaign", schema = Schemas.COUPONVALIDATOR, indexes = {
        @Index(name = "idx_coupon_code", columnList = "coupon_code")
})
public class Campaign implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "coupon_code", unique = true)
    private String couponCode;

    @Column(name = "max_redemptions")
    private int maxRedemptions;

    @Column(name = "redemption_count")
    private int redemptionCount;

    @Column(name = "per_customer_redemptions")
    private short perCustomerRedemptions;

    @Column(name = "discount_percentage")
    private short discountPercentage;

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public int getMaxRedemptions() {
        return maxRedemptions;
    }

    public int getRedemptionCount() {
        return redemptionCount;
    }

    public short getPerCustomerRedemptions() {
        return perCustomerRedemptions;
    }

    public short getDiscountPercentage() {
        return discountPercentage;
    }
}
