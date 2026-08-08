package com.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExcelUtils {
    private static Workbook workbook;
    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    private ExcelUtils() {
    }

    public static synchronized Workbook setExcel(String excelFilePath) {
        String path = requireNotBlank(excelFilePath, "excelFilePath");
        closeExcel();
        try (FileInputStream inputStream = new FileInputStream(new File(path))) {
            workbook = createWorkbook(inputStream, path);
            return workbook;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Excel file: " + path, e);
        }
    }

    public static synchronized void closeExcel() {
        if (workbook == null) {
            return;
        }
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close workbook", e);
        } finally {
            workbook = null;
        }
    }

    public static synchronized Map<String, List<String>> getData(String sheetName) {
        Workbook activeWorkbook = requireWorkbook();
        Sheet sheet = requireSheet(activeWorkbook, sheetName);
        Row headerRow = requireHeaderRow(sheet, sheetName);

        List<String> headers = readHeaders(headerRow);
        Map<String, List<String>> data = new LinkedHashMap<>();
        for (int col = 0; col < headers.size(); col++) {
            String header = headers.get(col);
            if (header.isBlank()) {
                continue;
            }

            List<String> columnData = new ArrayList<>();
            for (int row = 1; row <= sheet.getLastRowNum(); row++) {
                Row currentRow = sheet.getRow(row);
                Cell cell = currentRow == null ? null : currentRow.getCell(col);
                columnData.add(DATA_FORMATTER.formatCellValue(cell));
            }
            data.put(header, columnData);
        }
        return data;
    }

    public static String getCellValueByRowAndColumnName(
            String filePath,
            String sheetName,
            String rowName,
            String columnName
    ) throws IOException {
        String resolvedFilePath = requireNotBlank(filePath, "filePath");
        String resolvedSheetName = requireNotBlank(sheetName, "sheetName");
        String resolvedRowName = requireNotBlank(rowName, "rowName");
        String resolvedColumnName = requireNotBlank(columnName, "columnName");

        try (FileInputStream inputStream = new FileInputStream(resolvedFilePath);
             Workbook localWorkbook = createWorkbook(inputStream, resolvedFilePath)) {
            Sheet sheet = requireSheet(localWorkbook, resolvedSheetName);
            Row headerRow = requireHeaderRow(sheet, resolvedSheetName);
            Map<String, Integer> columnMap = buildColumnMap(headerRow);

            Integer targetColumnIndex = columnMap.get(resolvedColumnName);
            if (targetColumnIndex == null) {
                return null;
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                String currentRowName = DATA_FORMATTER.formatCellValue(row.getCell(0));
                if (resolvedRowName.equals(currentRowName)) {
                    return DATA_FORMATTER.formatCellValue(row.getCell(targetColumnIndex));
                }
            }
            return null;
        }
    }

    public static synchronized List<String> getDataList(String sheetName, String columnName) {
        String resolvedColumnName = requireNotBlank(columnName, "columnName");
        Map<String, List<String>> data = getData(sheetName);
        List<String> values = data.get(resolvedColumnName);
        return values == null ? Collections.emptyList() : values;
    }

    private static Workbook createWorkbook(FileInputStream inputStream, String excelFilePath) throws IOException {
        String normalized = excelFilePath.toLowerCase();
        if (normalized.endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        }
        if (normalized.endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        }
        throw new IllegalArgumentException("Unsupported Excel file type: " + excelFilePath);
    }

    private static Workbook requireWorkbook() {
        if (workbook == null) {
            throw new IllegalStateException("Workbook is not initialized. Call setExcel() first.");
        }
        return workbook;
    }

    private static Sheet requireSheet(Workbook workbook, String sheetName) {
        String resolvedSheetName = requireNotBlank(sheetName, "sheetName");
        Sheet sheet = workbook.getSheet(resolvedSheetName);
        if (sheet == null) {
            throw new IllegalArgumentException("Sheet not found: " + resolvedSheetName);
        }
        return sheet;
    }

    private static Row requireHeaderRow(Sheet sheet, String sheetName) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalStateException("Header row is missing in sheet: " + sheetName);
        }
        return headerRow;
    }

    private static List<String> readHeaders(Row headerRow) {
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(DATA_FORMATTER.formatCellValue(cell).trim());
        }
        return headers;
    }

    private static Map<String, Integer> buildColumnMap(Row headerRow) {
        Map<String, Integer> columnMap = new LinkedHashMap<>();
        for (Cell cell : headerRow) {
            String headerName = DATA_FORMATTER.formatCellValue(cell).trim();
            if (!headerName.isBlank()) {
                columnMap.put(headerName, cell.getColumnIndex());
            }
        }
        return columnMap;
    }

    private static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
