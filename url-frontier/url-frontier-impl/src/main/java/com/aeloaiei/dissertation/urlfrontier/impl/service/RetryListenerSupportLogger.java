package com.aeloaiei.dissertation.urlfrontier.impl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class RetryListenerSupportLogger extends RetryListenerSupport {
    private static final Logger LOGGER = LogManager.getLogger(RetryListenerSupportLogger.class);

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (nonNull(throwable)) {
            LOGGER.error("Failed to call method {} after {} attempts",
                    context.getAttribute("context.name"), context.getRetryCount(), throwable);
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        LOGGER.debug("Retry {}: Retryable method {} threw exception: {}",
                context.getRetryCount(), context.getAttribute("context.name"), throwable.toString());
    }
}
