package com.pan.excel.utils;

import com.pan.excel.entity.ExcelBusinessTable;
import com.pan.excel.entity.ExcelMappingTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.pan.excel.utils.SqlStrUtil.KEY_GENERATE_UUID;

/**
 * excel处理工具
 */
public class ExcelUtil {

    /**
     * 获取单元格内容
     *
     * @param cell 单元格
     * @param type 单元格内容类型
     * @return 单元格内容
     */
    public static Object getCellValue(Cell cell, String type) {
        if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
            return null;
        }

        return switch (type) {
            case "String" -> cell.getStringCellValue();
            case "Long", "Double" -> cell.getNumericCellValue();
            case "Boolean" -> cell.getBooleanCellValue();
            case "LocalDateTime" -> cell.getLocalDateTimeCellValue();
            case "LocalDate" -> cell.getLocalDateTimeCellValue().toLocalDate();
            case "Date" -> cell.getDateCellValue();
            default -> "";
        };
    }

    public static void saveSheetData(Sheet sheet, ExcelBusinessTable excelBusinessTable, Map<Integer, ExcelMappingTable> excelHeaderToMapping, JdbcTemplate jdbcTemplate) {
        // 读取Excel表头,起始行-1就是表头
        Row headerRow = sheet.getRow(excelBusinessTable.getStartRowIndex() - 1);

        // 开始解析数据行并生成SQL语句
        StringBuilder sqlBuilder = SqlStrUtil.buildSqlString(excelBusinessTable.getBusinessTableName(), excelHeaderToMapping, excelBusinessTable);

        // 初始化读取数据
        List<Object[]> dataArray = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum(); // 获取最后一行的索引
        int lastCellNum = headerRow.getLastCellNum(); // 获取表头行的最后一个单元格的索引
        int startRowIndex = excelBusinessTable.getStartRowIndex(); // 开始读取的行索引
        int startColumnIndex = excelBusinessTable.getStartColumnIndex(); // 开始读取的列索引
        int columnIndex; // 定义列索引变量

        // 遍历每一行数据
        for (int i = startRowIndex; i <= lastRowNum; i++) {
            Row dataRow = sheet.getRow(i); // 获取当前行
            Object[] rowData; // 初始化当前行的数据数组

            // 根据业务逻辑确定rowData数组的大小
            if (KEY_GENERATE_UUID.equals(excelBusinessTable.getBusinessIndexKeyGenerate())) {
                rowData = new Object[lastCellNum - startColumnIndex + 1]; // 如果是UUID，数组大小需要额外+1
                rowData[0] = UUID.randomUUID().toString(); // 在数组第一个位置存储UUID
                columnIndex = 1; // 设置列索引为1，因为已经使用了一个位置存储UUID
            } else {
                rowData = new Object[lastCellNum - startColumnIndex]; // 正常情况下的数组大小
                columnIndex = 0; // 设置列索引为0，因为不需要额外的列
            }

            // 遍历当前行的每一个单元格
            for (int j = startColumnIndex; j < lastCellNum; j++) {
                Cell cell = dataRow.getCell(j); // 获取当前单元格
                ExcelMappingTable mapping = excelHeaderToMapping.getOrDefault(j + startColumnIndex, null); // 根据单元格的索引获取对应的Excel映射

                if (mapping == null) {
                    continue; // 如果没有映射，则跳过该单元格
                }

                // 处理关联字段或普通字段
                if (mapping.getRelation()) { // 如果是关联字段
                    Object value = cell.getStringCellValue(); // 获取单元格的值
                    // 根据关联关系查询id
                    Object relationalId = SqlStrUtil.getRelationIdFromTable(mapping.getRelationTableFieldToFind(), mapping.getRelationTableName(), mapping.getRelationTableField(), value, mapping.getFieldType(), jdbcTemplate);
                    rowData[columnIndex++] = relationalId; // 将关联id存入数据数组中，并递增列索引
                } else { // 如果是普通字段
                    Object cellValue = ExcelUtil.getCellValue(cell, mapping.getFieldType()); // 获取单元格的值
                    rowData[columnIndex++] = cellValue; // 将值存入数据数组中，并递增列索引
                }
            }
            dataArray.add(rowData); // 将当前行的数据数组添加到数据集合中
        }
        // 执行保存
        SqlStrUtil.executeSQLList(sqlBuilder.toString(), dataArray, jdbcTemplate);
    }
}
