package com.lzy.nacosnotify.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lzy.nacosnotify.entity.FeishuCard;
import com.lzy.nacosnotify.service.FeishuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GrafanaController {

    @Autowired
    private FeishuService feishuService;

    @PostMapping("/log/alert")
    public void logAlert(@RequestBody String body){
        JSONObject jsonObject = JSONUtil.parseObj(body);
        String status = jsonObject.getStr("status");
        if (!"firing".equals(status)) {
            return;
        }
        JSONObject annotations = jsonObject.getJSONArray("alerts").getJSONObject(0).getJSONObject("annotations");
        String header = annotations.getStr("header");
        String description = annotations.getStr("description");
        FeishuCard feishuCard = new FeishuCard().generateCard(header, description);
        feishuCard.setCardHeaderColor(FeishuCard.RED);
        log.info("log alert: header:{}",header);
        feishuService.sendFeishuCard(feishuCard);
    }

    @PostMapping("/node/alert")
    public void nodeAlert(@RequestBody String body){
        JSONObject jsonObject = JSONUtil.parseObj(body);
        String status = jsonObject.getStr("status");
        JSONObject annotations = jsonObject.getJSONArray("alerts").getJSONObject(0).getJSONObject("annotations");
        String header = annotations.getStr("header");
        String description = annotations.getStr("description");
        FeishuCard feishuCard = new FeishuCard().generateCard(header, description);
        if ("firing".equals(status)) {
            feishuCard.setCardHeaderColor(FeishuCard.RED);
        }else {
            feishuCard.setCardHeaderColor(FeishuCard.GREEN);
        }
        log.info("node alert: header:{}",header);
        feishuService.sendFeishuCard(feishuCard);
    }



}
