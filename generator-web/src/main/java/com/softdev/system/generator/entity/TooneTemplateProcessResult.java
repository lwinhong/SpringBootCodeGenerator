package com.softdev.system.generator.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class TooneTemplateProcessResult implements Serializable {

    String id;//id
    String name;//名称
    String content;//处理之后的内容
    String path;//相对路径
    String extend;//后缀名
    String description;//描述
}
