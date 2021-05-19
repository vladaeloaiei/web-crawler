package com.aeloaiei.dissertation.domain.explorer.config;

import com.aeloaiei.dissertation.domain.explorer.dns.DNSResolver;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.SingletonDnsServerAddressStreamProvider;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;

import static io.netty.resolver.ResolvedAddressTypes.IPV4_PREFERRED;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${config.user.agent}")
    public String userAgent;

    @Value("${config.metadata.update.window}")
    public int metadataUpdateWindow;

    @Value("${config.resource.request.delay}")
    public int resourceRequestDelay;

    @Value("${config.resource.request.timeout}")
    public int resourceRequestTimeout;

    @Value("${config.resource.request.max.count}")
    public int resourceRequestMaxCount;

    @Value("${config.concurrent.domains.explorer.count}")
    public int concurrentDomainsExplorerCount;

    @Value("${config.dns.server.name}")
    public String dnsServerName;

    @Value("${config.dns.server.port}")
    public int dnsServerPort;

    @Bean
    public OkHttpClient getHttpClient(DNSResolver dnsResolver, Configuration config) {
        return new OkHttpClient.Builder()
                .dns(dnsResolver)
                .retryOnConnectionFailure(false)
                .followRedirects(true)
                .readTimeout(config.resourceRequestTimeout, MILLISECONDS)
                .build();
    }

    @Bean
    public DnsNameResolver getDnsNameResolver(Configuration config) {
        return new DnsNameResolverBuilder()
                .nameServerProvider(new SingletonDnsServerAddressStreamProvider(new InetSocketAddress(config.dnsServerName, config.dnsServerPort)))
                .channelType(NioDatagramChannel.class)
                .maxQueriesPerResolve(config.resourceRequestMaxCount)
                .recursionDesired(true)
                .queryTimeoutMillis(config.resourceRequestTimeout)
                .optResourceEnabled(false)
                .resolvedAddressTypes(IPV4_PREFERRED)
                .eventLoop(new NioEventLoopGroup().next())
                .build();
    }
}
