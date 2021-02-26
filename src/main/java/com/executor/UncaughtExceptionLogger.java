package com.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionLogger.class);

    public UncaughtExceptionLogger() {
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error("An uncaught exception is causing thread {} to exit!", t.getName(), e);
    }
}
