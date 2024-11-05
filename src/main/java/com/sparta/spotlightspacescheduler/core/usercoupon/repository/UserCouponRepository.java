package com.sparta.spotlightspacescheduler.core.usercoupon.repository;

import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.COUPON_NOT_FOUND;

import com.sparta.spotlightspacescheduler.common.exception.ApplicationException;
import com.sparta.spotlightspacescheduler.core.usercoupon.domain.UserCoupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCouponRepository extends JpaRepository<UserCoupon,Long>{

    List<UserCoupon> findAllByUserId(long userId);

    @Query("select u from UserCoupon u "
            + "join fetch u.coupon c "
            + "where c.id = :couponId and u.user.id = :userId"
    )
    Optional<UserCoupon> findByIdAndUserId(@Param("couponId") long couponId, @Param("userId") long userId);

    default UserCoupon findByCouponIdAndUserIdOrElseThrow(long couponId, long userId) {
        return findByIdAndUserId(couponId, userId).orElseThrow(() -> new ApplicationException(COUPON_NOT_FOUND));
    }
}
