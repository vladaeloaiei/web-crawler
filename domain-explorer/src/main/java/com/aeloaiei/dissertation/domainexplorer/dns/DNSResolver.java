package com.aeloaiei.dissertation.domainexplorer.dns;

import io.netty.resolver.dns.DnsNameResolver;
import okhttp3.Dns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Component
public class DNSResolver implements Dns {
    /* For log purpose */
    private static final Logger LOGGER = LogManager.getLogger(DNSResolver.class);

    @Autowired
    private DnsNameResolver resolver;

    @NonNull
    @Override
    public List<InetAddress> lookup(@NonNull String host) throws UnknownHostException {
        try {
            return resolver.resolveAll(host)
                    .get();
        } catch (Exception e) {
            LOGGER.error("Failed to resolve host: " + host, e);
            throw new UnknownHostException(e.getMessage());
        }
    }
}
