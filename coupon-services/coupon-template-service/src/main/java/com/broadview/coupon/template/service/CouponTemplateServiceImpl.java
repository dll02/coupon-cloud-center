package com.broadview.coupon.template.service;


import com.broadview.coupon.shared.beans.TemplateInfo;
import com.broadview.coupon.shared.enums.CouponType;
import com.broadview.coupon.template.beans.TemplateRequest;
import com.broadview.coupon.template.converter.CouponTemplateConverter;
import com.broadview.coupon.template.dao.CouponTemplateRepository;
import com.broadview.coupon.template.entity.CouponTemplate;
import com.broadview.coupon.template.entity.CouponTemplateEntity;
import com.broadview.coupon.template.service.intf.CouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 优惠券模板类相关操作
 */
@Slf4j
@Service
public class CouponTemplateServiceImpl  implements CouponTemplateService {

    @Autowired
    private CouponTemplateRepository templateDao;
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 创建优惠券模板
     */
    @Override
    public CouponTemplate createTemplate(TemplateRequest request) {
        // 单个门店最多可以创建100张优惠券模板
        if (request.getShopId() != null) {
            Integer count = templateDao.countByShopIdAndAvailable(request.getShopId(), true);
            if (count >= 100) {
                log.error("the totals of coupon template exceeds maximum number");
                throw new UnsupportedOperationException("exceeded the maximum of coupon templates that you can create");
            }
        }

        // 创建优惠券
        CouponTemplateEntity template = CouponTemplateEntity.builder()
                .name(request.getName())
                .description(request.getDesc())
                .total(request.getTotal())
                .category(CouponType.of(request.getType()))
                .available(true)
                .expired(false)
                .shopId(request.getShopId())
                .rule(request.getRule())
                .build();
        template = templateDao.save(template);

        return CouponTemplateConverter.convertToTemplate(template);
    }

    @Override
    public List<TemplateInfo> searchTemplate(TemplateRequest request) {
        CouponTemplateEntity example = CouponTemplateEntity.builder()
                .shopId(request.getShopId())
                .category(CouponType.of(request.getType()))
                .available(request.getAvailable())
                .name(request.getName())
                .build();
        // 可以用下面的方式做分页查询
//        Pageable page = PageRequest.of(0, 100);
//        templateDao.findAll(Example.of(example), page);
        List<CouponTemplateEntity> result = templateDao.findAll(Example.of(example));
        return result.stream()
                .map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toList());
    }

    /**
     * 通过ID查询优惠券模板
     */
    @Override
    @Cacheable(value = "templates", key = "#id")
    public TemplateInfo loadTemplateInfo(Long id) {
        Optional<CouponTemplateEntity> template = templateDao.findById(id);
        if (!template.isPresent()) {
            return null;
        }
        return CouponTemplateConverter.convertToTemplateInfo(template.get());
    }


    // 将券无效化
    @Override
    @Transactional
    @CacheEvict(value = "templates", key = "#id")
    public void inactiveCoupon(Long id) {
        int rows = templateDao.makeCouponUnavailable(id);
        if (rows == 0) {
            throw new IllegalArgumentException("Template Not Found: " + id);
        }
    }

    /**
     * 批量读取模板
     */
    @Override
    public Map<Long, TemplateInfo> getTemplateInfoMap(Collection<Long> ids) {

        List<CouponTemplateEntity> templates = templateDao.findAllById(ids);

        return templates.stream()
                .map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toMap(TemplateInfo::getId, Function.identity()));
    }

}
