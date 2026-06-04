package dev.pigmeo.couponvalidator.infrastructure.repositories.campaign;

import dev.pigmeo.couponvalidator.domain.entities.campaign.Campaign;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findByCouponCode(String couponCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Campaign c WHERE c.couponCode = :couponCode")
    Optional<Campaign> findByCouponCodeWithLock(@Param("couponCode") String couponCode);

    @Modifying
    @Query("UPDATE Campaign c SET c.redemptionCount = c.redemptionCount + 1 WHERE c.couponCode = :couponCode")
    void incrementCount(@Param("couponCode") String couponCode);
}
