package com.toone.system.generator.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TooneSchemeDevTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    String group;
    TooneTemplateProcessResult[] templates;
}
