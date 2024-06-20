package com.toone.system.generator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
public class Json2JavaInfo implements Serializable {
    TableInfo table;
    List<ColumnInfo> fields;

    @Data
    public static class TableInfo implements Serializable {
        String name;
        String comment;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ColumnInfo extends TableInfo {
        String dataType;
    }
}
