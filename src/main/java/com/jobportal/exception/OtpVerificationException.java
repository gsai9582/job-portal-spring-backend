package com.jobportal.exception;

public class OtpVerificationException extends RuntimeException {

    private final Reason reason;

    public OtpVerificationException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

    public enum Reason {
        INVALID,
        EXPIRED,
        COOLDOWN,
        TOO_MANY_ATTEMPTS,
        NOT_FOUND,
        ALREADY_VERIFIED
    }
}
