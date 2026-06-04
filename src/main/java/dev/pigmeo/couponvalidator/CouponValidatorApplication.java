package dev.pigmeo.couponvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CouponValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponValidatorApplication.class, args);
	}

}
