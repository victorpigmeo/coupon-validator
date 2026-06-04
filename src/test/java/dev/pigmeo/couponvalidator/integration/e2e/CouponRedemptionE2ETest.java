package dev.pigmeo.couponvalidator.integration.e2e;

import dev.pigmeo.couponvalidator.CouponValidatorBaseIntegrationTest;
import dev.pigmeo.couponvalidator.application.dto.ErrorResponse;
import dev.pigmeo.couponvalidator.application.dto.coupon.CouponRedeemRequest;
import dev.pigmeo.couponvalidator.application.dto.coupon.CouponRedeemResponse;
import dev.pigmeo.couponvalidator.domain.models.subscription.Tier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class CouponRedemptionE2ETest extends CouponValidatorBaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRedeemCoupon() {
        CouponRedeemRequest request =
                new CouponRedeemRequest(Tier.TRIAL, new BigDecimal("100.00"), "SUMMER26", 1L);

        long startTime = System.nanoTime();
        ResponseEntity<CouponRedeemResponse> response =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/v1/coupons/redeem",
                        request,
                        CouponRedeemResponse.class);
        long endTime = System.nanoTime();

        long latecyMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println(response);
        System.out.println("Latency: " + latecyMs + "ms");
    }

    @Test
    void shouldReceiveErrorWhenTierIsNotPaid() {
        CouponRedeemRequest request =
                new CouponRedeemRequest(Tier.TRIAL, new BigDecimal("100.00"), "SUMMER26", 1L);

        long startTime = System.nanoTime();
        ResponseEntity<ErrorResponse> response =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/v1/coupons/redeem",
                        request,
                        ErrorResponse.class);
        long endTime = System.nanoTime();

        long latecyMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println(response);
        System.out.println("Latency: " + latecyMs + "ms");
    }
}
