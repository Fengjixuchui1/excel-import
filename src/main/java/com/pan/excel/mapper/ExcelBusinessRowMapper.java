package com.pan.excel.mapper;

import com.pan.excel.entity.ExcelBusinessTable;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExcelBusinessRowMapper implements RowMapper<ExcelBusinessTable> {
    @Override
    public ExcelBusinessTable mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExcelBusinessTable excelBusinessTable = new ExcelBusinessTable();
        excelBusinessTable.setId(rs.getLong("id"));
        excelBusinessTable.setBusinessType(rs.getString("business_type"));
        excelBusinessTable.setBusinessTableName(rs.getString("business_table_name"));
        excelBusinessTable.setStartRowIndex(rs.getInt("start_row_index"));
        excelBusinessTable.setStartColumnIndex(rs.getInt("start_column_index"));
        excelBusinessTable.setBusinessIndexKey(rs.getString("business_index_key"));
        excelBusinessTable.setBusinessIndexKeyGenerate(rs.getString("business_index_key_generate"));
        excelBusinessTable.setBusinessSheetIndex(rs.getInt("business_sheet_index"));
        excelBusinessTable.setBusinessDatabaseType(rs.getString("business_database_type"));
        return excelBusinessTable;
    }
}