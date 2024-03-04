package com.pan.excel.service.impl;

import com.pan.excel.service.ExcelCheckTableService;
import com.pan.excel.utils.SqlStrUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

import static com.pan.excel.constants.Constants.*;

@Service
public class ExcelCheckTableServiceImpl implements ExcelCheckTableService {

    @Resource
    @Qualifier("dynamicJdbcTemplates")
    private Map<String, JdbcTemplate> dynamicJdbcTemplates;

    @Resource
    @Qualifier("dynamicDataSources")
    private Map<String, DataSource> dynamicDataSources;

    @Override
    public void checkTable() {
        for (Map.Entry<String, DataSource> entry : dynamicDataSources.entrySet()) {
            // 获取变量
            String name = entry.getKey();
            DataSource datasource = entry.getValue();
            String databaseProductName;
            boolean checkResult;

            // 获取jdbcTemplate
            JdbcTemplate jdbcTemplate = dynamicJdbcTemplates.get(name);

            // 获取数据库类型
            try {
                databaseProductName = datasource.getConnection().getMetaData().getDatabaseProductName();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // 判断数据库类型，执行对应建表语句
            switch (databaseProductName) {
                case MYSQL ->
                        checkResult = SqlStrUtil.checkTargetTable(jdbcTemplate, MYSQL_CHECK_BUSINESS_DDL, MYSQL_EXCEL_BUSINESS_DDL) && SqlStrUtil.checkTargetTable(jdbcTemplate, MYSQL_CHECK_MAPPING_DDL, MYSQL_EXCEL_MAPPING_DDL);
                case POSTGRESQL ->
                        checkResult = SqlStrUtil.checkTargetTable(jdbcTemplate, POSTGRESQL_CHECK_BUSINESS_DDL, POSTGRESQL_EXCEL_BUSINESS_DDL) && SqlStrUtil.checkTargetTable(jdbcTemplate, POSTGRESQL_CHECK_MAPPING_DDL, POSTGRESQL_EXCEL_MAPPING_DDL);
                default -> checkResult = false;
            }

            // 入股失败，则直接抛出异常
            if (!checkResult) {
                throw new RuntimeException("表初始化失败");
            }
        }

    }
}
