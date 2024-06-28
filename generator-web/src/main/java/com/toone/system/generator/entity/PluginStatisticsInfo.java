package com.toone.system.generator.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PluginStatisticsInfo {
    String appId;
    String ide;
    String ideVersion;
    String pluginVersion;
    String ip;
    String hostName;
    String os;
    String user;

    public String toString() {
        return "PluginStatisticsInfo{" +
                "appId='" + appId + '\'' +
                ", ide='" + ide + '\'' +
                ", ideVersion='" + ideVersion + '\'' +
                ", pluginVersion='" + pluginVersion + '\'' +
                ", ip='" + ip + '\'' +
                ", hostName='" + hostName + '\'' +
                ", os='" + os + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
