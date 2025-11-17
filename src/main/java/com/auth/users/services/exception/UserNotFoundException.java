package com.auth.users.services.exception;

import com.auth.common.error.BusinessException;
import com.auth.common.utils.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public UserNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
