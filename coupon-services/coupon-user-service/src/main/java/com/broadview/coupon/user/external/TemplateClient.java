package com.broadview.coupon.user.external;


import com.broadview.coupon.shared.beans.TemplateInfo;
import com.broadview.coupon.user.external.fallback.TemplateClientFallback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 券模板Feign接口
 */
@FeignClient(value = "coupon-template-service",fallback = TemplateClientFallback.class)
public interface TemplateClient {
    @RequestMapping(value = "/template/getBatch", method = RequestMethod.GET)
    Map<Long,TemplateInfo> getTemplateBatch(@RequestParam("ids") Collection<Long> ids);

    @GetMapping("/template/get")
    TemplateInfo getTemplate(@RequestParam("id")Long id);
}

/**
 * 券模板接口，不能使用Feign
 */
//@Component
//public class TemplateClient {
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Value("broadview.templateserivce.url.batch")
//    private String batchUrl;
//
//    @Value("broadview.templateserivce.url.single")
//    private String singleUrl;
//
//    public Map<Long, TemplateInfo> getTemplateBatch(@RequestParam("ids") Collection<Long> ids) {
//        // url,responseType, uriVariables
//        // ResponseEntity re = restTemplate.getForEntity( "http://localhost:20000/template/getBatch", Map.class, Collections.singletonMap("ids", ids));
//        ResponseEntity re = restTemplate.getForEntity( "http://localhost:20000/template/getBatch?ids={ids}", Map.class, StringUtils.join(ids,","));
//        return (Map<Long, TemplateInfo>) re.getBody();
//    }
//
//    public TemplateInfo getTemplate(@RequestParam("id") Long id) {
//        return restTemplate.getForEntity("http://localhost:20000/template/get?id={id}", TemplateInfo.class, Collections.singletonMap("id", id)).getBody();
//    }
//
//}
