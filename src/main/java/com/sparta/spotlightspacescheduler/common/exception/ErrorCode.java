package com.sparta.spotlightspacescheduler.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    FORBIDDEN_USER(FORBIDDEN, "권한이 없습니다."),
    INVALID_REFRESH_TOKEN(FORBIDDEN, "리프레시토큰이 없습니다"),
    INVALID_EMAIL_MATCH(FORBIDDEN, "인증번호가 일치하지 않습니다"),
    SOCIAL_LOGIN_UPDATE_NOT_ALLOWED(FORBIDDEN, "소셜 로그인 사용자는 회원 정보를 수정할 수 없습니다."),
    USER_NOT_FOUND(NOT_FOUND, "존재하지 않는 유저입니다."),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "리프레시토큰이 없습니다"),
    INVALID_PASSWORD_OR_EMAIL(NOT_FOUND, "이메일 또는 패스워드가 일치하지 않습니다"),
    EMAIL_DUPLICATED(CONFLICT, "이미 존재하는 이메일입니다."),
    RESERVED_TICKET_CANCELLATION_REQUIRED(CONFLICT, "예약된 티켓을 취소하고 다시 시도해주세요."),
    RESERVED_EVENT_CANCELLATION_REQUIRED(CONFLICT, "진행중인 이벤트를 취소하고 다시 시도해주세요."),
    RESERVED_CALCULATION_REQUIRED(CONFLICT, "완료되지 않은 정산이 있습니다."),
    ADMIN_NOT_FOUND(NOT_FOUND, "존재하지 않는 관리자입니다."),
    ADMIN_PASSWORD_MISMATCH(FORBIDDEN, "어드민 암호가 일치하지 않습니다."),
    
    TICKET_PRICE_CANNOT_BE_NEGATIVE(BAD_REQUEST, "티켓 가격은 음수일 수 없습니다."),
    EVENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 이벤트입니다."),

    ATTACHMENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 첨부파일입니다."),

    TICKET_NOT_FOUND(NOT_FOUND, "존재하지 않는 티켓입니다."),

    NOT_ENOUGH_POINT_AMOUNT(BAD_REQUEST, "포인트가 부족합니다."),
    POINT_AMOUNT_CANNOT_BE_NEGATIVE(BAD_REQUEST, "사용할 포인트는 음수일 수 없습니다."),
    POINT_NOT_FOUND(NOT_FOUND, "존재하지 않는 포인트입니다."),

    POINT_HISTORY_NOT_FOUND(NOT_FOUND, "존재하지 않는 포인트 기록입니다."),

    EVENT_TICKET_OUT_OF_STOCK(BAD_REQUEST, "이벤트 티켓 재고가 없습니다"),
    EVENT_TICKET_STOCK_NOT_FOUND(NOT_FOUND, "존재하지 않는 이벤트 티켓 재고입니다."),

    COUPON_ALREADY_USED(BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    COUPON_NOT_FOUND(NOT_FOUND, "존재하지 않는 쿠폰입니다."),
    COUPON_EXPIRED(BAD_REQUEST, "만료된 쿠폰입니다."),
    COUPON_COUNT_EXHAUSTED(CONFLICT, "발급 가능한 쿠폰 수량이 부족합니다."),
    COUPON_ALREADY_ISSUED(CONFLICT, "해당 쿠폰이 이미 발급되었습니다."),

    INVALID_PAYMENT_STATUS(BAD_REQUEST, "유효하지 않은 결제 상태입니다."),
    NOT_IN_EVENT_RECRUITMENT_PERIOD(BAD_REQUEST, "이벤트 모집 기간이 아닙니다."),
    TID_NOT_FOUND(NOT_FOUND, "결제 고유 번호가 존재하지 않습니다."),
    CANCELLATION_PERIOD_EXPIRED(BAD_REQUEST, "결제 취소 가능 기간이 아닙니다."),
    PAYMENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 결제입니다."),
    PAYMENT_EVENT_NOT_FOUND(NOT_FOUND, "존재하지 않는 결제 이벤트입니다."),

    REVIEW_NOT_FOUND(NOT_FOUND, "존재하지 않는 리뷰입니다."),

    UNAUTHORIZED(NOT_FOUND, "티켓을 결제한 사용자만 리뷰를 작성 할 수 있습니다."),

    WRONG_CATEGORY_NAME(BAD_REQUEST, "유효하지 않는 카테고리 입니다."),

    USER_NOT_ARTIST(FORBIDDEN, "일반 유저는 해당 작업을 수행할 수 없습니다."),

    TABLE_NOT_FOUND(NOT_FOUND, "테이블이 존재하지 않습니다."),

    USER_NOT_ACCESS_EVENT(FORBIDDEN, "해당 사용자가 등록한 이벤트가 아닙니다."),

    USER_NOT_ACCESS_REVIEW(FORBIDDEN, "해당 사용자가 등록한 리뷰가 아닙니다."),

    CANNOT_MAX_PEOPLE_UPDATE(BAD_REQUEST, "현제 결제된 인원보다 작은 수로 인원 설정이 불가합니다."),

    NO_RESULTS_FOUND(NOT_FOUND, "검색 결과가 없습니다."),

    JSON_PROCESSING_EXCEPTION(BAD_REQUEST, "Json 처리 중 오류가 발생했습니다."),
    
    LOCK_NOT_ACQUIRED(FORBIDDEN, "해당 요청은 락을 획득할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
