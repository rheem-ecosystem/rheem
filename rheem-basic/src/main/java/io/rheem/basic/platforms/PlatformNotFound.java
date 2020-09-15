package io.rheem.basic.platforms;

//TODO: put the documentation
public class PlatformNotFound extends RuntimeException{

    public PlatformNotFound() {
        super();
    }

    public PlatformNotFound(String message) {
        super(message);
    }

    public PlatformNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public PlatformNotFound(Throwable cause) {
        super(cause);
    }

    protected PlatformNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
