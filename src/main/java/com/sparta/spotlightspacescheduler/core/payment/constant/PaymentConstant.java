package com.sparta.spotlightspacescheduler.core.payment.constant;

public abstract class PaymentConstant {

    public static final String APPROVAL_URL = "http://localhost:8080/api/v1/payments/approve";
    public static final String CANCEL_URL = "http://localhost:8080/api/v1/payments/cancel";
    public static final String FAIL_URL = "http://localhost:8080/api/v1/payments/fail";
    public static final String PAYMENT_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    public static final String PAYMENT_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    public static final String PAYMENT_CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";
}
