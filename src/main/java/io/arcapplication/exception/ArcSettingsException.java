package io.arcapplication.exception;

public class ArcSettingsException extends  Exception{
    public ArcSettingsException() {
    }

    public ArcSettingsException(String message) {
        super(message);
    }

    public ArcSettingsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArcSettingsException(Throwable cause) {
        super(cause);
    }

    public ArcSettingsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
