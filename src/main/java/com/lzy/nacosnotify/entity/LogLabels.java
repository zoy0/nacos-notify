package com.lzy.nacosnotify.entity;

import lombok.Data;

@Data
public class LogLabels {
    private String app;

    private String alertTime;

    private String level;

    private String logger;

    private String message;

}
