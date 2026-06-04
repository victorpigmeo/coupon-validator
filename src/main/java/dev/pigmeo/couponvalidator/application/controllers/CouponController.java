package dev.pigmeo.couponvalidator.application.controllers;

import dev.pigmeo.couponvalidator.application.dto.coupon.CouponRedeemRequest;
import dev.pigmeo.couponvalidator.application.dto.coupon.CouponRedeemResponse;
import dev.pigmeo.couponvalidator.application.mappers.CouponMapper;
import dev.pigmeo.couponvalidator.domain.services.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/coupons")
public class CouponController {

    private final CouponMapper couponMapper;
    private final CouponService couponService;

    public CouponController(CouponMapper couponMapper, CouponService couponService) {
        this.couponMapper = couponMapper;
        this.couponService = couponService;
    }

    @PostMapping("/redeem")
    public ResponseEntity<CouponRedeemResponse> redeemCoupon(
            @Valid @RequestBody CouponRedeemRequest couponRedeemRequest) {
        return ResponseEntity.ok(
                couponMapper.toRedeemResponse(
                        couponService.redeemCoupon(
                                couponMapper.toRedeemCouponCommand(couponRedeemRequest))));
    }
}
