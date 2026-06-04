package dev.pigmeo.couponvalidator;

import dev.pigmeo.couponvalidator.config.TestContainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
public abstract class CouponValidatorBaseIntegrationTest {

}
