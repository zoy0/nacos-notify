package com.lzy.nacosnotify.service.impl;


import com.lzy.nacosnotify.entity.FeishuCard;
import com.lzy.nacosnotify.service.FeishuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class FeishuServiceImpl implements FeishuService {

    @Value("${feishu.webhookUrl}")
    private String webhookUrl;

    @Autowired
    @Qualifier(value = "restTemplate")
    private RestTemplate restTemplate;

    @Override
    public void sendFeishuCard(FeishuCard feishuCard) {
        MultiValueMap<String, String> requestHeader = new LinkedMultiValueMap();
        requestHeader.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        requestHeader.put(HttpHeaders.ACCEPT, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        HttpEntity<FeishuCard> requestBody = new HttpEntity<>(feishuCard, requestHeader);
        ResponseEntity<String> exchangeResult = restTemplate.exchange(webhookUrl, HttpMethod.POST, requestBody, String.class);
    }
}
