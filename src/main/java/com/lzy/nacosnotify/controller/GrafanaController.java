package com.lzy.nacosnotify.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lzy.nacosnotify.entity.FeishuCard;
import com.lzy.nacosnotify.entity.LogLabels;
import com.lzy.nacosnotify.service.FeishuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

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
        List<LogLabels> list = JSONUtil.toList(annotations.getJSONArray("json"), LogLabels.class);
        Set<LogLabels> set = new TreeSet<>(Comparator.comparing(LogLabels::getMessage));
        set.addAll(list);
        set.forEach(logLabel -> {
            String description = "**服务**:"+logLabel.getApp()+"\n"+
                    "**时间**:"+logLabel.getAlertTime()+"\n"+
                    "**等级**:"+logLabel.getLevel()+"\n"+
                    "**Logger类**:"+logLabel.getLogger()+"\n"+
                    "**具体信息**\n:"+logLabel.getMessage();
            FeishuCard feishuCard = new FeishuCard().generateCard(header, description);
            feishuCard.setCardHeaderColor(FeishuCard.RED);
            log.info("log alert: header:{}\ndescription:{}",header,description);
            feishuService.sendFeishuCard(feishuCard);
        });

    }

    @PostMapping("/node/alert")
    public void nodeAlert(@RequestBody String body){
        JSONObject jsonObject = JSONUtil.parseObj(body);
        String status = jsonObject.getStr("status");
        JSONObject annotations = jsonObject.getJSONArray("alerts").getJSONObject(0).getJSONObject("annotations");
        String header = annotations.getStr("header");
        String description = annotations.getStr("description");
        FeishuCard feishuCard = new FeishuCard().generateCard(null, description);
        if ("firing".equals(status)) {
            header = "告警:" + header;
            feishuCard.setCardHeaderColor(FeishuCard.RED);
        }else {
            header = "已解决:" + header;
            feishuCard.setCardHeaderColor(FeishuCard.GREEN);
        }
        feishuCard.setHeader(header);
        log.info("node alert: header:{}\ndescription:{}",header,description);
        feishuService.sendFeishuCard(feishuCard);
    }



}
