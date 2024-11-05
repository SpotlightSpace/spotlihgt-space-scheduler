package com.sparta.spotlightspacescheduler.core.point.point.domain;

import com.sparta.spotlightspacescheduler.core.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private int amount;

    public boolean cannotDeduct(int pointAmount) {
        return amount < pointAmount;
    }

    public void deduct(int amount) {
        this.amount -= amount;
    }

    public void addPoint(int amount) {
        this.amount += amount;
    }

    private Point(int amount, User user) {
        this.amount = amount;
        this.user = user;
    }

    public static Point of(int amount, User user) {
        return new Point(amount, user);
    }

    public void cancelUsage(int amount) {
        this.amount += amount;
    }
}
