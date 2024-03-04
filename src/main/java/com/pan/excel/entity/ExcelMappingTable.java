package com.pan.excel.entity;

import lombok.Data;

/**
 * Excel字段关系对应表
 */
@Data
public class ExcelMappingTable {
    private Long id;
    private Long relationMasterId;
    private String fieldType;
    private Integer fieldNo;
    private String businessTableName;
    private String businessTableFieldName;
    private Boolean relation;
    private String relationTableName;
    private String relationTableField;
    private String relationTableFieldToFind;
}
