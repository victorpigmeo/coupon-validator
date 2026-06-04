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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class CouponRedemptionE2ETest extends CouponValidatorBaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRedeemCoupon() throws InterruptedException {
        int maxRedemptions = 30;
        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        List<Integer> responseStatusCodes = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            CouponRedeemRequest request = new CouponRedeemRequest(
                    Tier.PAID,
                    new BigDecimal("100.00"),
                    "SUMMER26", (long) i);

            executorService.submit(() -> {
                try {
                    startLatch.await();
                    ResponseEntity<CouponRedeemResponse> response = restTemplate.postForEntity(
                            "http://localhost:" + port + "/v1/coupons/redeem",
                            request,
                            CouponRedeemResponse.class);
                    responseStatusCodes.add(response.getStatusCode().value());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });

        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        long successRequestCount = responseStatusCodes
                .stream()
                .filter((statusCode) -> {return statusCode == 200;})
                .count();

        long failureRequestCount = responseStatusCodes
                .stream()
                .filter((statusCode) -> {return statusCode != 200;})
                .count();


        assertThat(successRequestCount).isEqualTo(maxRedemptions);
        assertThat(failureRequestCount).isEqualTo(threadCount - maxRedemptions);

    }

    @Test
    void shouldReceiveErrorWhenTierIsNotPaid() {
        CouponRedeemRequest request =
                new CouponRedeemRequest(Tier.TRIAL, new BigDecimal("100.00"), "SUMMER26", 1L);

        ResponseEntity<ErrorResponse> response =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/v1/coupons/redeem",
                        request,
                        ErrorResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Coupons are only for paid plans");
    }
}
