package org.eulerframework.common.http;

class ExceptionEraser {
    static <T extends RuntimeException> T asRuntimeException(final Throwable throwable) {
        return ExceptionEraser.eraseType(throwable);
    }

    @SuppressWarnings("unchecked")
    private static <R, T extends Throwable> R eraseType(final Throwable throwable) throws T {
        throw (T) throwable;
    }
}
