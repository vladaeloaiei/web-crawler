package com.aeloaiei.dissertation.domainexplorer.service.dns;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.SingletonDnsServerAddressStreamProvider;
import okhttp3.Dns;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

@Component
public class DNSResolver implements Dns {
    /* For log purpose */
    private static final Logger LOGGER = LogManager.getLogger(DNSResolver.class);
    private static final String DNS_SERVER = "8.8.8.8";
    private static final int DNS_PORT = 53;
    private static final int QUERY_MAX_RETRIES = 3;
    private static final int QUERY_TIMEOUT = 1000;

    private DnsNameResolver resolver;

    public DNSResolver() {
        resolver = new DnsNameResolverBuilder()
                .nameServerProvider(new SingletonDnsServerAddressStreamProvider(new InetSocketAddress(DNS_SERVER, DNS_PORT)))
                .channelType(NioDatagramChannel.class)
                .maxQueriesPerResolve(QUERY_MAX_RETRIES)
                .recursionDesired(true)
                .queryTimeoutMillis(QUERY_TIMEOUT)
                .optResourceEnabled(false)
                .resolvedAddressTypes(ResolvedAddressTypes.IPV4_PREFERRED)
                .eventLoop(new NioEventLoopGroup().next())
                .build();
    }

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
