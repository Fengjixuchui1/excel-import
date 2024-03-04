package com.pan.excel.service;

import jakarta.annotation.PostConstruct;

public interface ExcelCheckTableService {

    void checkTable();

    @PostConstruct
    default void check() {
        checkTable();
    }
}
