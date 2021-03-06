package com.aeloaiei.dissertation.domain.explorer.http;

import com.aeloaiei.dissertation.urlfrontier.api.dto.UniformResourceLocatorDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
public class HTTPResourceRetriever {
    private static final Logger LOGGER = LogManager.getLogger(HTTPResourceRetriever.class);

    @Autowired
    private OkHttpClient httpClient;
    @Autowired
    private HTTPResourceFilter httpResourceFilter;

    public Optional<RawWebResource> retrieve(UniformResourceLocatorDto url, String userAgent) {
        Optional<RawWebResource> rawWebResource = Optional.empty();
        Request request = new Request.Builder()
                .url(url.getLocation())
                .addHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOGGER.debug("Unable to retrieve resource: " + url.getLocation() + " http code: " + response.code());
                rawWebResource = Optional.of(new RawWebResource(url.getLocation(), "", false));
            } else if (isNull(response.body())) {
                LOGGER.debug("Resource: " + url.getLocation() + " has empty body");
                rawWebResource = Optional.of(new RawWebResource(url.getLocation(), "", false));
            } else {
                LOGGER.debug("Resource: " + url.getLocation() + " successfully retrieved");

                if (httpResourceFilter.filter(response)) {
                    rawWebResource = Optional.of(new RawWebResource(url.getLocation(), response.body().string(), false));
                } else {
                    rawWebResource = Optional.of(new RawWebResource(url.getLocation(), response.body().string(), true));
                }
            }

            url.setHttpStatus(HttpStatus.resolve(response.code()));
        } catch (IOException e) {
            LOGGER.error("Failed to retrieve resource " + url.getLocation(), e);
        }

        return rawWebResource;
    }
}
