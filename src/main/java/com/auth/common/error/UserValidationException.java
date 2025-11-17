package com.auth.common.error;

import com.auth.common.utils.ErrorCode;

public class UserValidationException extends BusinessException {
    public UserValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UserValidationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
