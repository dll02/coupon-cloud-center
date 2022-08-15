package com.broadview.coupon.user.external.fallback;

import com.broadview.coupon.shared.beans.TemplateInfo;
import com.broadview.coupon.user.external.TemplateClient;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

// fegin注解指定降级类的过程实际是一个依赖注入过程
@Slf4j
@Component
public class TemplateClientFallback implements TemplateClient {
    @Override
    public Map<Long, TemplateInfo> getTemplateBatch(Collection<Long> ids) {
        log.info("fallback logic, ids={}",ids);
        return Maps.newHashMap();
    }

    @Override
    public TemplateInfo getTemplate(Long id) {
        log.info("fallback logic, ids={}",id);
        return null;
    }
}
