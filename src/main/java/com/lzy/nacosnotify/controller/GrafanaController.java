package com.lzy.nacosnotify.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lzy.nacosnotify.entity.FeishuCard;
import com.lzy.nacosnotify.entity.LogLabels;
import com.lzy.nacosnotify.service.FeishuService;
import com.lzy.nacosnotify.util.EscapeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
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
    public void logAlert(@RequestBody String body) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(body);
            String status = jsonObject.getStr("status");
            if (!"firing".equals(status)) {
                return;
            }
            JSONObject annotations = jsonObject.getJSONArray("alerts").getJSONObject(0).getJSONObject("annotations");
            String header = annotations.getStr("header");
            Set<LogLabels> set = new TreeSet<>(Comparator.comparing(LogLabels::getMessage));
            String res = EscapeUtil.escape(annotations.getStr("json"));
            List<LogLabels> list = JSONUtil.toList(JSONUtil.parseArray(res), LogLabels.class);
            set.addAll(list);

            set.forEach(logLabel -> {
                if(logLabel.getMessage().length()>500){
                    logLabel.setMessage(logLabel.getMessage().substring(0,500)+"\n......");
                }
                String description = "**服务**:" + logLabel.getApp() + "\n" +
                        "**时间**:" + logLabel.getAlertTime() + "\n" +
                        "**等级**:" + logLabel.getLevel() + "\n" +
                        "**Logger类**:" + logLabel.getLogger() + "\n" +
                        "**具体信息**:\n" + logLabel.getMessage();
                FeishuCard feishuCard = new FeishuCard().generateCard(header, description);
                feishuCard.setCardHeaderColor(FeishuCard.RED);
                feishuService.sendFeishuCard(feishuCard);
            });
        } catch (Exception e) {
            log.error("==========log alert==========");
            log.error("{}",body);
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/node/alert")
    public void nodeAlert(@RequestBody String body) {
        try {
            JSONObject jsonObject = JSONUtil.parseObj(body);
            String status = jsonObject.getStr("status");
            JSONObject annotations = jsonObject.getJSONArray("alerts").getJSONObject(0).getJSONObject("annotations");
            String header = annotations.getStr("header");
            String description = annotations.getStr("description");
            FeishuCard feishuCard = new FeishuCard().generateCard(null, description);
            if ("firing".equals(status)) {
                header = "告警:" + header;
                feishuCard.setCardHeaderColor(FeishuCard.RED);
            } else {
                header = "已解决:" + header;
                feishuCard.setCardHeaderColor(FeishuCard.GREEN);
            }
            feishuCard.setHeader(header);
            feishuService.sendFeishuCard(feishuCard);
        } catch (Exception e) {
            log.error("==========node alert==========");
            log.error("{}",body);
            throw new RuntimeException(e);
        }
    }



}
