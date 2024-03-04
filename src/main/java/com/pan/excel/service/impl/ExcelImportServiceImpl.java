package com.pan.excel.service.impl;

import com.pan.excel.entity.ExcelBusinessTable;
import com.pan.excel.entity.ExcelMappingTable;
import com.pan.excel.service.ExcelImportService;
import com.pan.excel.utils.ExcelUtil;
import com.pan.excel.utils.SqlStrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    @Resource
    @Qualifier("defaultJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Resource
    @Qualifier("dynamicJdbcTemplates")
    private Map<String, JdbcTemplate> dynamicJdbcTemplates;

    /**
     * 导入单个excel，单张sheet表
     *
     * @param businessType 业务类型
     * @param file         excel文件
     */
    @Override
    public void importSheet(String businessType, MultipartFile file) {
        if (file.isEmpty()) {
            return;
        }

        try (InputStream inputStream = file.getInputStream()) {
            // 获取指定工作表
            Workbook workbook = WorkbookFactory.create(inputStream);
            // 读取数据库中的映射关系
            ExcelBusinessTable excelBusinessTable = SqlStrUtil.findByBusinessType(businessType, jdbcTemplate);
            // 处理并保存数据
            processAndSaveData(excelBusinessTable, workbook);
        } catch (IOException | EncryptedDocumentException e) {
            log.error("导入Excel出错:{}", e.getMessage(), e);
        }
    }


    /**
     * 导入多个sheet页
     *
     * @param businessType 业务类型
     * @param file         excel文件
     */
    @Override
    public void importSheetBatch(String businessType, MultipartFile file) {
        if (file.isEmpty()) {
            return;
        }

        try (InputStream inputStream = file.getInputStream()) {
            // 获取指定工作表
            Workbook workbook = WorkbookFactory.create(inputStream);
            // 读取数据库中的映射关系
            List<ExcelBusinessTable> excelBusinessTables = SqlStrUtil.findByBusinessTypeContaining(businessType, jdbcTemplate);
            // 循环处理并保存数据
            for (ExcelBusinessTable excelBusinessTable : excelBusinessTables) {
                processAndSaveData(excelBusinessTable, workbook);
            }
        } catch (IOException | EncryptedDocumentException e) {
            log.error("导入Excel出错:{}", e.getMessage(), e);
        }
    }

    /**
     * 处理每张sheet页数据并保存
     *
     * @param excelBusinessTable 业务主表
     * @param workbook           工作本
     */
    private void processAndSaveData(ExcelBusinessTable excelBusinessTable, Workbook workbook) {
        Map<Integer, ExcelMappingTable> excelHeaderToMapping = new LinkedHashMap<>();

        // 获取要入库的数据源
        JdbcTemplate targetJdbcTemplate = dynamicJdbcTemplates.get(excelBusinessTable.getBusinessDatabaseType());

        // 获取字段对应关系
        SqlStrUtil.findAllByRelationMasterId(excelBusinessTable.getId(), jdbcTemplate).forEach(mapping -> excelHeaderToMapping.put(mapping.getFieldNo(), mapping));
        // 获取sheet页
        if (excelBusinessTable.getBusinessSheetIndex() > workbook.getNumberOfSheets() - 1) {
            return;
        }
        Sheet sheet = workbook.getSheetAt(excelBusinessTable.getBusinessSheetIndex());
        // 解析工作表，并保存数据
        ExcelUtil.saveSheetData(sheet, excelBusinessTable, excelHeaderToMapping, targetJdbcTemplate);
    }
}
