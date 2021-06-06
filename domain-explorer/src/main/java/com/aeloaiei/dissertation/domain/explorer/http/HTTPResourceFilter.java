package com.aeloaiei.dissertation.domain.explorer.http;

import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

@Component
public class HTTPResourceFilter {
    private static final Logger LOGGER = LogManager.getLogger(HTTPResourceFilter.class);

    private static final Set<String> DENIED_CONTENT_TYPE = new HashSet<>(asList("audio", "font/", "image", "video", "model"));
    private static final int CONTENT_TYPE_PREFIX_SIZE = 5;

    public boolean filter(Response response) {
        String contentTypeHeader = response.header(HttpHeaders.CONTENT_TYPE);

        if (nonNull(contentTypeHeader)) {
            return DENIED_CONTENT_TYPE.contains(contentTypeHeader.substring(CONTENT_TYPE_PREFIX_SIZE));
        } else {
            return false;
        }
    }
}
