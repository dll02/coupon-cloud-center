package com.broadview.coupon.user;

import com.broadview.coupon.user.mq.RequestCouponQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

// feign组件在启动阶段将@Feign接口加入spring上下文
@EnableFeignClients(basePackages = {"com.broadview"})
@EnableDiscoveryClient
@EnableCircuitBreaker //开启熔断降级功能
@EnableJpaAuditing
@SpringBootApplication
@ComponentScan(basePackages = {"com.broadview"})
@EnableBinding(value = {
        RequestCouponQueue.class
}
)//加载Stream 生产者和消费者信道
public class CouponUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponUserApplication.class, args);
    }

    @Bean
    feign.Logger.Level feignLoggerInfo() {
        return feign.Logger.Level.FULL;
    }

/**
 * Feign 不需要注入RestTemplate
 */
//    @Bean
//    @LoadBalanced
//    RestTemplate restTemplate() {
//        RestTemplate restTemplate = new RestTemplate();
//        return restTemplate;
//    }
}

