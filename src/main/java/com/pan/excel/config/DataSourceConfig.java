package com.pan.excel.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


@Data
@Component
@ConfigurationProperties(prefix = "spring")
public class DataSourceConfig {

    private Map<String, DataSourceProperties> datasource;

    @Bean
    @Qualifier("dynamicDataSources")
    public Map<String, DataSource> dynamicDataSources() {
        Map<String, DataSource> dynamicDataSources = new LinkedHashMap<>();
        for (Map.Entry<String, DataSourceProperties> entry : datasource.entrySet()) {
            String name = entry.getKey();
            DataSourceProperties properties = entry.getValue();
            dynamicDataSources.put(name, createDataSource(properties));
        }
        return dynamicDataSources;
    }

    @Bean
    @Qualifier("dynamicJdbcTemplates")
    public Map<String, JdbcTemplate> dynamicJdbcTemplates(@Qualifier("dynamicDataSources") Map<String, DataSource> dynamicDataSources) {
        Map<String, JdbcTemplate> dynamicJdbcTemplates = new LinkedHashMap<>();
        for (Map.Entry<String, DataSource> entry : dynamicDataSources.entrySet()) {
            String name = entry.getKey();
            DataSource dataSource = entry.getValue();
            dynamicJdbcTemplates.put(name, new JdbcTemplate(dataSource));
        }
        return dynamicJdbcTemplates;
    }

    @Bean
    @Qualifier("defaultJdbcTemplate")
    public JdbcTemplate defaultJdbcTemplate(@Qualifier("dynamicJdbcTemplates") Map<String, JdbcTemplate> dynamicJdbcTemplates) {
        Optional<JdbcTemplate> defaultJdbcTemplateOptional = dynamicJdbcTemplates.values().stream().findFirst();
        return defaultJdbcTemplateOptional.orElse(null);
    }

    private DataSource createDataSource(DataSourceProperties properties) {
        return DataSourceBuilder.create()
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }

}
