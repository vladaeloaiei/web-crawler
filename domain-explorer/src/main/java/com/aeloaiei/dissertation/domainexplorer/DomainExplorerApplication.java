package com.aeloaiei.dissertation.domainexplorer;


import com.aeloaiei.dissertation.domainexplorer.config.Configuration;
import com.aeloaiei.dissertation.domainexplorer.dns.DNSResolver;
import com.aeloaiei.dissertation.domainexplorer.service.MultiDomainExplorerDaemon;
import com.aeloaiei.dissertation.domainexplorer.service.StorageDaemon;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.SingletonDnsServerAddressStreamProvider;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.resolver.ResolvedAddressTypes.IPV4_PREFERRED;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@EnableFeignClients(basePackages = {
        "com.aeloaiei.dissertation.domainfeeder.api",
        "com.aeloaiei.dissertation.urlfrontier.api",
        "com.aeloaiei.dissertation.documenthandler.api"
})
@SpringBootApplication
public class DomainExplorerApplication implements CommandLineRunner {
    private static final Logger LOGGER = LogManager.getLogger(DomainExplorerApplication.class);

    @Autowired
    private MultiDomainExplorerDaemon multiDomainExplorerDaemon;
    @Autowired
    private StorageDaemon storageDaemon;

    public static void main(String[] args) {
        SpringApplication.run(DomainExplorerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(multiDomainExplorerDaemon);
        executor.submit(storageDaemon);
    }

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
