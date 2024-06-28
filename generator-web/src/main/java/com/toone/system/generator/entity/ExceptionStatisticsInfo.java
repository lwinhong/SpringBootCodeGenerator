package com.toone.system.generator.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExceptionStatisticsInfo {
    PluginStatisticsInfo pluginInfo;

    String message;
    String stackTrace;
}
