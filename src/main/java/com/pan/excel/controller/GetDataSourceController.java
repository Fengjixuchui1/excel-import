package com.pan.excel.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/getDataSource")
public class GetDataSourceController {

    @Resource
    @Qualifier("dynamicDataSources")
    private Map<String, DataSource> dynamicDataSources;

    /**
     * 获取已经配置的数据源，方便前端写入业务数据库类型
     *
     * @return 系统已经配置的所有数据源
     */
    @GetMapping
    public Set<String> getDataSourceList() {
        return dynamicDataSources.keySet();
    }

}
