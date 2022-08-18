package com.broadview.coupon.user.controller;

import com.broadview.coupon.shared.beans.CouponInfo;
import com.broadview.coupon.shared.beans.PlaceOrder;
import com.broadview.coupon.user.entity.Coupon;
import com.broadview.coupon.user.mq.RequestCouponQueue;
import com.broadview.coupon.user.pojo.RequestCoupon;
import com.broadview.coupon.user.service.intf.CouponUserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("coupon-user")
@EnableHystrix // 多级降级
@RefreshScope //监听属性变更广播事件
public class CouponController {

    @Autowired
    private CouponUserService couponUserService;

    @Value("${request-coupon-disabled:false}")
    private boolean disableRequestingCoupon;

    @Autowired
    private RequestCouponQueue requestCouponQueue;

    @GetMapping("findCoupon")
    public List<CouponInfo> findCoupon(@RequestParam("userId") Long userId,
                                       @RequestParam(value = "status", required = false) Integer status,
                                       @RequestParam(value = "shopId", required = false) Long shopId) {
        return couponUserService.findCoupon(userId, status, shopId);
    }

    @PostMapping("requestCoupon")
    @HystrixCommand(fallbackMethod = "requestCouponFallback",
            // commandKey = "requestCouponKey",
            //注解一个commandKey属性，yml中通过commandKey属性添加指定hystrix参数
            commandProperties = {
                    // 设置超时时间 2000ms，优先级高于yml，等价于hystrix.command.default配置属性
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
            })
    public Coupon requestCoupon(@Valid @RequestBody RequestCoupon request) throws InterruptedException {
        if (disableRequestingCoupon) {
            log.info("disable requesting coupon");
            return null;
        }
        log.info("request Coupon normal");
        return couponUserService.requestCoupon(request);
    }

    public Coupon requestCouponFallback(RequestCoupon request) {
        log.info("requestCoupon fallback");
        return Coupon.builder().build();
    }

    @PostMapping("requestCouponQueue")
    public String requestCouponQueue(@Valid @RequestBody RequestCoupon request) {
        log.info("ready to send message, {}", request);
        Message message = MessageBuilder
                .withPayload(request)
                // 延迟消费需要额外安装插件
                //.setHeader("x-delay", 3 * 1000)
                .build();
        log.info("message is {}", message);
        // 生产者信道
        boolean success = requestCouponQueue.output().send(message);
        log.info("send requestCouponQueue success? {}", success);

        return success ? "请稍后到账户查询" : "发券失败";
    }


    @PostMapping("placeOrder")
    public PlaceOrder checkout(@Valid @RequestBody PlaceOrder info) {
        return couponUserService.placeOrder(info);
    }

    @PostMapping("inactiveCoupon")
    @GlobalTransactional(name = "coupon-user-service", rollbackFor = Exception.class)
    public Integer inactiveCoupon(@RequestParam("templateId") Long temlateId) throws InterruptedException {
        return couponUserService.inactiveCouponTemplate(temlateId);
    }
}
