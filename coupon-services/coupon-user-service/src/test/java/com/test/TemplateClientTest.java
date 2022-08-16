package com.test;

//import com.broadview.coupon.shared.beans.TemplateInfo;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TemplateClientTest {


//    @Autowired
//    private RestTemplate restTemplate;

//    @Value("broadview.templateserivce.url.single")
//    private String singleUrl;

    public static void main(String[] args) {

//        System.out.println(singleUrl);

         //restTemplate.getForEntity("http://localhost:20000/template/get", TemplateInfo.class, Collections.singletonMap("id", id)).getBody();

        String url = "http://localhost:20000/template/getBatch";
        RestTemplate restTemplate = new RestTemplate();
        Collection<Long> ids=new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("ids",1L);
        Collections.singletonMap("ids", ids);
        StringUtils.join(ids,",");
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
//                .query(queryParam)
                .buildAndExpand(paramMap);

        ResponseEntity re = restTemplate.getForEntity( "http://localhost:20000/template/getBatch?ids={ids}", Map.class, StringUtils.join(ids,","));



//        ResponseEntity<String> zhimaAuthResponse = restTemplate.getForEntity(url, String.class, paramMap);
//        String zhimaAuthResponse = restTemplate.getForObject(url, String.class, paramMap);
        System.out.println(re.getStatusCode());
        if (HttpStatus.OK == re.getStatusCode()) {
            System.out.println(re.getBody());
            System.out.println(re.getHeaders());
        }
    }

    public void test01(){
        String url = "http://localhost:20000/template/get?id={id}";
        RestTemplate restTemplate = new RestTemplate();

        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("id",1L);



        ResponseEntity<String> zhimaAuthResponse = restTemplate.getForEntity(url, String.class, paramMap);
//        String zhimaAuthResponse = restTemplate.getForObject(url, String.class, paramMap);
        System.out.println(zhimaAuthResponse.getStatusCode());
        if (HttpStatus.OK == zhimaAuthResponse.getStatusCode()) {
            System.out.println(zhimaAuthResponse.getBody());
            System.out.println(zhimaAuthResponse.getHeaders());
        }
    }
}
