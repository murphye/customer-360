package io.solo.customer360.nameservice;

import java.util.Collections;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.netty.channel.unix.DomainSocketAddress;
import reactor.netty.http.server.HttpServer;

@Configuration
public class NettyConfig {

    @Bean
    @Profile("istio-domainsockets")
    public NettyReactiveWebServerFactory factory() {
        var factory = new NettyReactiveWebServerFactory();
        factory.setServerCustomizers(Collections.singletonList(new NettyServerCustomizer() {
            @Override
            public HttpServer apply(HttpServer httpServer) {
                return httpServer.bindAddress(() -> new DomainSocketAddress("/var/run/name/inbound.sock"));
            }
        }));
        return factory;
    }
}