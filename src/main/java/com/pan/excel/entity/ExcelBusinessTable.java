package com.pan.excel.entity;

import lombok.Data;

@Data
public class ExcelBusinessTable {
    private Long id;
    private String businessType;
    private String businessTableName;
    private Integer startRowIndex;
    private Integer startColumnIndex;
    private String businessIndexKey;
    private String businessIndexKeyGenerate;
    private Integer businessSheetIndex;
    private String businessDatabaseType;

}
