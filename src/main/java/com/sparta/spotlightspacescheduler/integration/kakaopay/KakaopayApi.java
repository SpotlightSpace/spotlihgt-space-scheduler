package com.sparta.spotlightspacescheduler.integration.kakaopay;


import static com.sparta.spotlightspacescheduler.common.exception.ErrorCode.JSON_PROCESSING_EXCEPTION;
import static com.sparta.spotlightspacescheduler.core.payment.constant.PaymentConstant.APPROVAL_URL;
import static com.sparta.spotlightspacescheduler.core.payment.constant.PaymentConstant.CANCEL_URL;
import static com.sparta.spotlightspacescheduler.core.payment.constant.PaymentConstant.FAIL_URL;
import static com.sparta.spotlightspacescheduler.core.payment.constant.PaymentConstant.PAYMENT_APPROVE_URL;
import static com.sparta.spotlightspacescheduler.core.payment.constant.PaymentConstant.PAYMENT_CANCEL_URL;
import static com.sparta.spotlightspacescheduler.core.payment.constant.PaymentConstant.PAYMENT_READY_URL;
import static com.sparta.spotlightspacescheduler.core.payment.constant.PaymentConstant.PAYMENT_STATUS_URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spotlightspacescheduler.common.exception.ApplicationException;
import com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay.KakaopayApprovePaymentResponseDto;
import com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay.KakaopayCancelPaymentResponseDto;
import com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay.KakaopayPaymentErrorResponseDto;
import com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay.KakaopayPaymentResponseDto;
import com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay.KakaopayPaymentStatusResponseDto;
import com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay.KakaopayReadyPaymentResponseDto;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaopayApi {

    private static final String SECRET_KEY_PREFIX = "SECRET_KEY ";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payment.kakao.secret.key}")
    private String secretKey;

    public KakaopayPaymentResponseDto readyPayment(
            String cid,
            long partnerOrderId,
            long userId,
            String eventTitle,
            long eventId,
            int totalPrice
    ) {
        Map<String, String> parameters = getParametersForReadyPayment(
                cid,
                partnerOrderId,
                userId,
                eventTitle,
                eventId,
                totalPrice
        );

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                PAYMENT_READY_URL,
                new HttpEntity<>(parameters, getHeaders()),
                String.class
        );

        KakaopayPaymentResponseDto responseDto = null;
        try {
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                responseDto = KakaopayPaymentResponseDto.ofSuccess(
                        objectMapper.readValue(responseEntity.getBody(), KakaopayReadyPaymentResponseDto.class)
                );
            } else if (responseEntity.getStatusCode().is4xxClientError() || responseEntity.getStatusCode()
                    .is5xxServerError()) {
                responseDto = KakaopayPaymentResponseDto.ofFail(
                        objectMapper.readValue(responseEntity.getBody(), KakaopayPaymentErrorResponseDto.class)
                );
            }
        } catch (JsonProcessingException e) {
            throw new ApplicationException(JSON_PROCESSING_EXCEPTION);
        }

        return responseDto;
    }

    public KakaopayPaymentResponseDto approvePayment(
            String pgToken,
            String tid,
            String cid,
            long paymentId,
            long userId
    ) {
        Map<String, String> parameters = getParametersForApprovePayment(pgToken, cid, tid, paymentId, userId);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                PAYMENT_APPROVE_URL,
                new HttpEntity<>(parameters, getHeaders()),
                String.class
        );

        KakaopayPaymentResponseDto responseDto = null;
        try {
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                responseDto = KakaopayPaymentResponseDto.ofSuccess(
                        objectMapper.readValue(responseEntity.getBody(), KakaopayApprovePaymentResponseDto.class)
                );
            } else if (responseEntity.getStatusCode().is4xxClientError() || responseEntity.getStatusCode()
                    .is5xxServerError()) {
                responseDto = KakaopayPaymentResponseDto.ofFail(
                        objectMapper.readValue(responseEntity.getBody(), KakaopayPaymentErrorResponseDto.class)
                );
            }
        } catch (JsonProcessingException e) {
            throw new ApplicationException(JSON_PROCESSING_EXCEPTION);
        }

        return responseDto;
    }

    public KakaopayPaymentResponseDto cancelPayment(String cid, String tid, int cancelAmount, int cancelTaxFreeAmount) {
        Map<String, String> parameters = getParametersForCancelPayment(cid, tid, cancelAmount, cancelTaxFreeAmount);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                PAYMENT_CANCEL_URL,
                new HttpEntity<>(parameters, getHeaders()),
                String.class
        );

        KakaopayPaymentResponseDto responseDto = null;
        try {
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                responseDto = KakaopayPaymentResponseDto.ofSuccess(
                        objectMapper.readValue(responseEntity.getBody(), KakaopayCancelPaymentResponseDto.class)
                );
            } else if (responseEntity.getStatusCode().is4xxClientError() || responseEntity.getStatusCode()
                    .is5xxServerError()) {
                responseDto = KakaopayPaymentResponseDto.ofFail(
                        objectMapper.readValue(responseEntity.getBody(), KakaopayPaymentErrorResponseDto.class)
                );
            }
        } catch (JsonProcessingException e) {
            throw new ApplicationException(JSON_PROCESSING_EXCEPTION);
        }

        return responseDto;
    }

    public KakaopayPaymentStatusResponseDto getPaymentStatus(String cid, String tid) {
        Map<String, String> parameters = getParametersForGetPaymentStatus(cid, tid);
        KakaopayPaymentStatusResponseDto responseDto = restTemplate.postForObject(
                PAYMENT_STATUS_URL,
                new HttpEntity<>(parameters, getHeaders()),
                KakaopayPaymentStatusResponseDto.class);

        return responseDto;
    }

    private Map<String, String> getParametersForGetPaymentStatus(String cid, String tid) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);
        return parameters;
    }

    private Map<String, String> getParametersForReadyPayment(
            String cid,
            long partnerOrderId,
            long userId,
            String eventTitle,
            long eventId,
            int totalPrice
    ) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tax_free_amount", "0");
        parameters.put("partner_order_id", String.valueOf(partnerOrderId));
        parameters.put("partner_user_id", String.valueOf(userId));
        parameters.put("item_name", eventTitle);
        parameters.put("item_code", String.valueOf(eventId));
        parameters.put("quantity", "1");
        parameters.put("total_amount", String.valueOf(totalPrice));
        parameters.put("approval_url", APPROVAL_URL);
        parameters.put("cancel_url", CANCEL_URL);
        parameters.put("fail_url", FAIL_URL);
        return parameters;
    }

    private Map<String, String> getParametersForApprovePayment(
            String pgToken,
            String cid,
            String tid,
            long partnerOrderId,
            long partnerUserId
    ) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);
        parameters.put("partner_order_id", String.valueOf(partnerOrderId));
        parameters.put("partner_user_id", String.valueOf(partnerUserId));
        parameters.put("pg_token", pgToken);
        return parameters;
    }

    private Map<String, String> getParametersForCancelPayment(
            String cid,
            String tid,
            int cancelAmount,
            int cancelTaxFreeAmount
    ) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", cid);
        parameters.put("tid", tid);
        parameters.put("cancel_amount", String.valueOf(cancelAmount));
        parameters.put("cancel_tax_free_amount", String.valueOf(cancelTaxFreeAmount));
        return parameters;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", SECRET_KEY_PREFIX + secretKey);
        headers.set("Content-type", "application/json");
        return headers;
    }
}
