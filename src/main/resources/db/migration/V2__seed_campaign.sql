INSERT INTO couponvalidator.campaign
(id, name, start_date, end_date, coupon_code, max_redemptions,
 per_customer_redemptions, redemption_count, discount_percentage)
VALUES (1, 'Test campaign',
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '1 day',
        'SUMMER26', 1000, 1,
        0, 10);
