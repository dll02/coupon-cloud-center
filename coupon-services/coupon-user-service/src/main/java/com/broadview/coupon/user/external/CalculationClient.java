package com.broadview.coupon.user.external;

import com.broadview.coupon.shared.beans.PlaceOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;


/**
 * 改为Feign接口注入
 */

@FeignClient(value = "coupon-calculator-service")
public interface CalculationClient {
    @RequestMapping(value = "/calculator/checkout", method = RequestMethod.POST)
    PlaceOrder computeRule(@RequestBody PlaceOrder order);
}

/**
 * 券模板接口，不能使用Feign
 *
 * @Component
 * public class CalculationClient {
 * @Autowired
 * private RestTemplate restTemplate;
 *
 *     @Value("broadview.calculatserivce.url")
 *    private String calcuationUrl;
 *
 * public PlaceOrder computeRule(PlaceOrder settlement) {
 * // url,request,response type
 * String calcuationUrl="http://localhost:20001/calculator/checkout";
 * return restTemplate.postForEntity(calcuationUrl, settlement, PlaceOrder.class).getBody();
 * }
 * }
 */
