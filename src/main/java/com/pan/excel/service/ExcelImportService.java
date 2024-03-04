package com.pan.excel.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelImportService {

    @Transactional
    void importSheet(String businessType, MultipartFile file);

    @Transactional
    void importSheetBatch(String businessType, MultipartFile file);

}
