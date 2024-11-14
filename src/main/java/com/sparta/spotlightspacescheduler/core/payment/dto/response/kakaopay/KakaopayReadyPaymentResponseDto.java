package com.sparta.spotlightspacescheduler.core.payment.dto.response.kakaopay;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaopayReadyPaymentResponseDto implements TidAccessible {

    /**
     * 결제 고유 번호
     */
    private final String tid;

    /**
     * 모바일 앱일 경우 카카오톡 결제 페이지 Redirect URL
     */
    private final String nextRedirectAppUrl;

    /**
     * 모바일 웹일 경우 카카오톡 결제 페이지 Redirect URL
     */
    private final String nextRedirectMobileUrl;

    /**
     * PC 웹일 경우 카카오톡으로 결제 요청 메시지(TMS)를 보내기 위한 사용자 정보 입력 화면 Redirect URL
     */
    private final String nextRedirectPcUrl;

    /**
     * 카카오페이 결제 화면으로 이동하는 Android 앱 스킴(Scheme) - 내부 서비스용
     */
    private final String androidAppScheme;

    /**
     * 카카오페이 결제 화면으로 이동하는 iOS 앱 스킴 - 내부 서비스용
     */
    private final String iosAppScheme;

    /**
     * 결제 준비 요청 시간
     */
    private final LocalDateTime createdAt;
}
