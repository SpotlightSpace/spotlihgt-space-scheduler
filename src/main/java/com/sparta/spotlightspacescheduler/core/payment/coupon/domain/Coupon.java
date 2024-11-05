package com.sparta.spotlightspacescheduler.core.payment.coupon.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private LocalDate expiredAt;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false, unique = true, length = 15)
    private String code;

    @Column(nullable = false)
    private Boolean isUsed = true;

    public static Coupon of(int discountAmount, LocalDate expiredAt, Integer count, String code) {
        return new Coupon(null, discountAmount, expiredAt, count, code, false);
    }

    public void update(int discountAmount, LocalDate expiredAt) {
        this.discountAmount = discountAmount;
        this.expiredAt = expiredAt;
    }

    public void setAsUnusable() {
        this.isUsed = true;
    }

}

