package com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaopayPaymentResponseDto<T extends TidAccessible> {

    private String status;
    private T response;

    private KakaopayPaymentResponseDto(String status, T response) {
        this.status = status;
        this.response = response;
    }

    public static <T extends TidAccessible> KakaopayPaymentResponseDto<T> ofSuccess(T response) {
        return new KakaopayPaymentResponseDto("success", response);
    }

    public static <T extends TidAccessible> KakaopayPaymentResponseDto<T> ofFail(T response) {
        return new KakaopayPaymentResponseDto("fail", response);
    }

    @JsonIgnore
    public String getTid() {
        return response.getTid();
    }
}
