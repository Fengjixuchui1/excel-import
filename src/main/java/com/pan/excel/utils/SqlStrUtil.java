package com.pan.excel.utils;

import com.pan.excel.entity.ExcelBusinessTable;
import com.pan.excel.entity.ExcelMappingTable;
import com.pan.excel.mapper.ExcelBusinessRowMapper;
import com.pan.excel.mapper.ExcelMappingRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * 用于生成各种sql语句
 */
public class SqlStrUtil {

    public static final String KEY_GENERATE_UUID = "UUID";

    private static final String FIND_BY_BUSINESS_TYPE = "SELECT * FROM excel_business WHERE business_type = ?";
    private static final String FIND_BY_BUSINESS_TYPE_CONTAINING = "SELECT * FROM excel_business WHERE business_type LIKE ?";
    private static final String FIND_ALL_BY_RELATION_MASTER_ID = "SELECT * FROM excel_mapping WHERE relation_master_id = ?";


    /**
     * 生成业务insert语句
     *
     * @param tableName            业务表名
     * @param excelHeaderToMapping 业务字段对应关系
     * @param business             主表名
     * @return 业务insert语句
     */
    public static StringBuilder buildSqlString(String tableName, Map<Integer, ExcelMappingTable> excelHeaderToMapping, ExcelBusinessTable business) {
        // 生成insert前缀语句
        StringBuilder sqlBuilder = new StringBuilder(String.format("INSERT INTO %s (", tableName));
        // 判断主键生成方式，如果是uuid则需要加上id字段
        if (KEY_GENERATE_UUID.equals(business.getBusinessIndexKeyGenerate())) {
            sqlBuilder.append(business.getBusinessIndexKey()).append(",");
        }
        excelHeaderToMapping.forEach((k, v) -> sqlBuilder.append(v.getBusinessTableFieldName()).append(","));
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(") VALUES (");
        // 判断主键生成方式，如果是uuid则需要加一个?
        if (KEY_GENERATE_UUID.equals(business.getBusinessIndexKeyGenerate())) {
            sqlBuilder.append("?").append(",");
        }
        excelHeaderToMapping.forEach((k, v) -> sqlBuilder.append("?").append(","));
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(")");
        return sqlBuilder;
    }

    /**
     * 生成查询业务主表语句
     *
     * @param businessType 业务类型
     * @param jdbcTemplate bean
     * @return 生成查询主表语句
     */
    public static ExcelBusinessTable findByBusinessType(String businessType, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForObject(FIND_BY_BUSINESS_TYPE, new ExcelBusinessRowMapper(), businessType);
    }

    /**
     * 生成根据业务类型批量查询业务主表语句
     *
     * @param business     业务类型
     * @param jdbcTemplate bean
     * @return 生成查询语句
     */
    public static List<ExcelBusinessTable> findByBusinessTypeContaining(String business, JdbcTemplate jdbcTemplate) {
        // 拼接模糊查询的参数
        String likeKeyword = "%" + business + "%";
        return jdbcTemplate.query(FIND_BY_BUSINESS_TYPE_CONTAINING, new ExcelBusinessRowMapper(), likeKeyword);
    }

    /**
     * 生成根据关联键查询业务字段对应关系
     *
     * @param relationMasterId 关联键
     * @param jdbcTemplate     bean
     * @return 生成查询语句
     */
    public static List<ExcelMappingTable> findAllByRelationMasterId(Long relationMasterId, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query(FIND_ALL_BY_RELATION_MASTER_ID, new ExcelMappingRowMapper(), relationMasterId);
    }

    /**
     * 执行批量插入sql
     *
     * @param sql          批量插入语句
     * @param dataArray    要插入的数据
     * @param jdbcTemplate bean
     */
    public static void executeSQLList(String sql, List<Object[]> dataArray, JdbcTemplate jdbcTemplate) {
        jdbcTemplate.batchUpdate(sql, dataArray);
    }

    /**
     * 查询关联字段
     *
     * @param tableField     关联字段
     * @param tableName      关联表名
     * @param tableFindField 关联要查询字段
     * @param value          要查询内容
     * @param clazz          返回类型
     * @param jdbcTemplate   bean
     * @return 查询结果
     */
    public static Object getRelationIdFromTable(String tableField, String tableName, String tableFindField, Object value, String clazz, JdbcTemplate jdbcTemplate) {
        String sql = "SELECT " + tableField + " FROM " + tableName + " WHERE " + tableFindField + " = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Class.forName("java.lang." + clazz), value);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查目标表是否存在，不存在则创建
     *
     * @param jdbcTemplate bean
     * @param checkSql     检查slq
     * @param executeSql   建表sql
     * @return 是否检查通过
     */
    public static Boolean checkTargetTable(JdbcTemplate jdbcTemplate, String checkSql, String executeSql) {
        if (Boolean.FALSE.equals(jdbcTemplate.queryForObject(checkSql, Boolean.class))) {
            jdbcTemplate.execute(executeSql);
            return jdbcTemplate.queryForObject(checkSql, Boolean.class);
        }
        return Boolean.TRUE;
    }
}
