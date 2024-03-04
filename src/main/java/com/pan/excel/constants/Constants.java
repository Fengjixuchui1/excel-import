package com.pan.excel.constants;

public class Constants {

    public static final String MYSQL = "MySQL";
    public static final String POSTGRESQL = "PostgreSQL";

    public static final String MYSQL_EXCEL_BUSINESS_DDL = """
            CREATE TABLE `excel_business` (
              `id` bigint NOT NULL AUTO_INCREMENT,
              `business_type` varchar(255) DEFAULT NULL,
              `business_table_name` varchar(255) DEFAULT NULL,
              `start_row_index` int DEFAULT NULL,
              `start_column_index` int DEFAULT NULL,
              `business_index_key` varchar(255) DEFAULT NULL,
              `business_index_key_generate` varchar(255) DEFAULT NULL,
              `business_sheet_index` int DEFAULT '0',
              `business_database_type` varchar(255) DEFAULT 'mysql',
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4""";

    public static final String MYSQL_EXCEL_MAPPING_DDL = """
            CREATE TABLE `excel_mapping` (
              `id` bigint NOT NULL AUTO_INCREMENT,
              `relation_master_id` bigint DEFAULT NULL,
              `field_type` varchar(255) DEFAULT NULL,
              `field_no` int DEFAULT NULL,
              `business_table_name` varchar(255) DEFAULT NULL,
              `business_table_field_name` varchar(255) DEFAULT NULL,
              `relation` tinyint(1) DEFAULT NULL,
              `relation_table_name` varchar(255) DEFAULT NULL,
              `relation_table_field` varchar(255) DEFAULT NULL,
              `relation_table_field_to_find` varchar(255) DEFAULT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4""";

    public static final String POSTGRESQL_EXCEL_BUSINESS_DDL = """
            CREATE TABLE excel_business (
              id SERIAL PRIMARY KEY,
              business_type VARCHAR(255),
              business_table_name VARCHAR(255),
              start_row_index INT,
              start_column_index INT,
              business_index_key VARCHAR(255),
              business_index_key_generate VARCHAR(255),
              business_sheet_index INT DEFAULT 0,
              business_database_type VARCHAR(255) DEFAULT 'mysql'
            )""";

    public static final String POSTGRESQL_EXCEL_MAPPING_DDL = """
            CREATE TABLE excel_mapping (
              id SERIAL PRIMARY KEY,
              relation_master_id BIGINT,
              field_type VARCHAR(255),
              field_no INT,
              business_table_name VARCHAR(255),
              business_table_field_name VARCHAR(255),
              relation BOOLEAN,
              relation_table_name VARCHAR(255),
              relation_table_field VARCHAR(255),
              relation_table_field_to_find VARCHAR(255)
            )""";

    public static final String MYSQL_CHECK_BUSINESS_DDL = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'excel_business')";
    public static final String MYSQL_CHECK_MAPPING_DDL = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'excel_mapping')";
    public static final String POSTGRESQL_CHECK_BUSINESS_DDL = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'excel_business')";
    public static final String POSTGRESQL_CHECK_MAPPING_DDL = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'excel_mapping')";

}
