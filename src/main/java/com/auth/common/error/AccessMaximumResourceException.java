package com.auth.common.error;

import com.auth.common.utils.ErrorCode;

public class AccessMaximumResourceException extends BusinessException {
    public AccessMaximumResourceException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public AccessMaximumResourceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AccessMaximumResourceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
