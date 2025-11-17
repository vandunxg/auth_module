package com.auth.common.error;

import com.auth.common.utils.ErrorCode;

public class AuthenticationException extends BusinessException {
    public AuthenticationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public AuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
