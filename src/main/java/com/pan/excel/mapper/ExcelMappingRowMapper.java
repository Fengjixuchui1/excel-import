package com.pan.excel.mapper;

import com.pan.excel.entity.ExcelMappingTable;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExcelMappingRowMapper implements RowMapper<ExcelMappingTable> {
    @Override
    public ExcelMappingTable mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExcelMappingTable excelMappingTable = new ExcelMappingTable();
        excelMappingTable.setId(rs.getLong("id"));
        excelMappingTable.setRelationMasterId(rs.getLong("relation_master_id"));
        excelMappingTable.setFieldType(rs.getString("field_type"));
        excelMappingTable.setFieldNo(rs.getInt("field_no"));
        excelMappingTable.setBusinessTableName(rs.getString("business_table_name"));
        excelMappingTable.setBusinessTableFieldName(rs.getString("business_table_field_name"));
        excelMappingTable.setRelation(rs.getBoolean("relation"));
        excelMappingTable.setRelationTableName(rs.getString("relation_table_name"));
        excelMappingTable.setRelationTableField(rs.getString("relation_table_field"));
        excelMappingTable.setRelationTableFieldToFind(rs.getString("relation_table_field_to_find"));
        return excelMappingTable;
    }
}
