package com.nexushr.common.exception;

/**
 * Custom exception for business rule violations across NexusHR.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
