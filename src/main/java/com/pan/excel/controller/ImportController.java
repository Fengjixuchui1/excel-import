package com.pan.excel.controller;

import com.pan.excel.service.ExcelImportService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
public class ImportController {

    @Resource
    private ExcelImportService excelImportService;

    @PostMapping
    public void importSheet(@RequestParam(name = "businessType") String businessType, @RequestParam(name = "file") MultipartFile file) {
        excelImportService.importSheet(businessType, file);
    }

    @PostMapping("/batch")
    public void importSheetBatch(@RequestParam(name = "businessType") String businessType, @RequestParam(name = "file") MultipartFile file) {
        excelImportService.importSheetBatch(businessType, file);
    }
}
