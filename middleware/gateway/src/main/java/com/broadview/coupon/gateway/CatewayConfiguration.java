package com.broadview.coupon.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 代码添加路由信息 可读性高
@Configuration
public class CatewayConfiguration {

    @Autowired
    private KeyResolver hostNameResolver;

    @Autowired
    @Qualifier("userRateLimiter")
    private RateLimiter rateLimiterUser;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder){
        return builder.routes()
                // path 谓词关键词 路径匹配
                .route(route->route.path("/coupon-user/**")
                        .filters(f->f.requestRateLimiter(c->{
                            c.setKeyResolver(hostNameResolver);
                        c.setRateLimiter(rateLimiterUser);}
                        )).uri("lb://COUPON-USER-SERVICE")
                ).route(route -> route
                        .path("/template/**")
                        .uri("lb://COUPON-TEMPLATE-SERVICE")
                ).route(route ->route
                        .path("/calculator/**")
                        .uri("lb://COUPON-CALCULATOR-SERVICE")
                ).build();
    }
}
