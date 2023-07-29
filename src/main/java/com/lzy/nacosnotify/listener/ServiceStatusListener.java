package com.lzy.nacosnotify.listener;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lzy.nacosnotify.conf.MonitorConfig;
import com.lzy.nacosnotify.entity.FeishuCard;
import com.lzy.nacosnotify.service.FeishuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@RefreshScope
public class ServiceStatusListener implements InitializingBean {

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private MonitorConfig config;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String nacosUrl;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;

    private static Map<String, Integer> cache = new ConcurrentHashMap<>();

    @PreDestroy
    public void preDestroy() {
        log.info("preDestroy....");
    }

    /**
     * 初始化监听服务上下线
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("afterPropertiesSet........");
        Properties properties = System.getProperties();
        properties.setProperty("serverAddr", nacosUrl);
        properties.setProperty("namespace", namespace);
        NamingService naming = NamingFactory.createNamingService(properties);
        List<String> serviceNames = config.getServices();
        log.info("需要监控的服务数:{}", serviceNames.size());
        for (String service : serviceNames) {
            naming.subscribe(service, event -> {
                List<Instance> instances = ((NamingEvent) event).getInstances();
                String serviceName = ((NamingEvent) event).getServiceName();
                if (!cache.containsKey(serviceName)){
                    cache.put(serviceName,0);
                }

                if(instances.size() <cache.get(serviceName)) {
                    log.info(serviceName +"服务下线,"+"服务异常下线告警,当前节点数："+instances.size());
                    if (config.getEnabled()) {
                        FeishuCard feishuCard = new FeishuCard().generateCard("服务下线", serviceName + "服务下线," + "服务异常下线告警,当前节点数：" + instances.size()+"\n<at id=all></at> ");
                        feishuCard.setCardHeaderColor(FeishuCard.RED);
                        feishuService.sendFeishuCard(feishuCard);
                    }
                    cache.put(serviceName, instances.size());
                }else if (instances.size() > cache.get(serviceName)){
                    log.info("============服务上线"+ serviceName+",当前节点数量："+instances.size());
                    if (config.getEnabled()) {
                        FeishuCard feishuCard = new FeishuCard().generateCard("服务上线", serviceName + "已上线,当前节点数量："+instances.size()+"\n<at id=all></at> ");
                        feishuCard.setCardHeaderColor(FeishuCard.GREEN);
                        feishuService.sendFeishuCard(feishuCard);
                    }
                    cache.put(serviceName, instances.size());
                }

            });
        }
    }
}
