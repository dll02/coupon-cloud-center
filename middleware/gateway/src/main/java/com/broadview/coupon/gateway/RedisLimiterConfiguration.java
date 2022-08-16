package com.broadview.coupon.gateway;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RedisLimiterConfiguration {
    @Bean
    @Primary
    public KeyResolver remoteAddrKeyResolver(){
        // 限流算法的key为hostaddress
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress());
    }

    @Bean("userRateLimiter")
    @Primary
    public RedisRateLimiter redisLimiterUser(){
        return new RedisRateLimiter(10,20);
    }
}
