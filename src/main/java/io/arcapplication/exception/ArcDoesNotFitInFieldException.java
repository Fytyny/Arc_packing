package io.arcapplication.exception;

public class ArcDoesNotFitInFieldException extends Exception {
    public ArcDoesNotFitInFieldException() {
    }

    public ArcDoesNotFitInFieldException(String message) {
        super(message);
    }

    public ArcDoesNotFitInFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArcDoesNotFitInFieldException(Throwable cause) {
        super(cause);
    }

    public ArcDoesNotFitInFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
