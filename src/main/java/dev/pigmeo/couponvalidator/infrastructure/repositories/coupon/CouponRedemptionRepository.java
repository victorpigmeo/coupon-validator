package dev.pigmeo.couponvalidator.infrastructure.repositories.coupon;

import dev.pigmeo.couponvalidator.domain.entities.coupon.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> { }
