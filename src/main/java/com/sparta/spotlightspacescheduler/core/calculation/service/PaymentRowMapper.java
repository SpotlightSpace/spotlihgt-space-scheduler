package com.sparta.spotlightspacescheduler.core.calculation.service;


import com.sparta.spotlightspacescheduler.core.event.domain.Event;
import com.sparta.spotlightspacescheduler.core.payment.domain.Payment;
import com.sparta.spotlightspacescheduler.core.user.domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

@Slf4j
public class PaymentRowMapper implements RowMapper<Payment> {

    @Override
    public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = User.of(
                rs.getLong("user_id")
        );

        Event event = Event.create(
                rs.getLong("event_id"),
                rs.getTimestamp("start_at").toLocalDateTime(),
                rs.getTimestamp("end_at").toLocalDateTime(),
                rs.getInt("price"),
                rs.getTimestamp("recruitment_start_at").toLocalDateTime(),
                rs.getTimestamp("recruitment_finish_at").toLocalDateTime(),
                rs.getBoolean("event_is_deleted"),
                rs.getBoolean("is_calculated"),
                user
        );

        return Payment.create(
                rs.getString("tid"),
                rs.getString("cid"),
                event,
                user,
                rs.getInt("original_amount"),
                null,
                null
        );
    }

}

