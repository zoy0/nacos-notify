package com.lzy.nacosnotify.conf;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class MonitorConfig {

    @Value("${monitor.enabled}")
    private Boolean enabled;

    @Value("${monitor.services}")
    private List<String> services;




}
