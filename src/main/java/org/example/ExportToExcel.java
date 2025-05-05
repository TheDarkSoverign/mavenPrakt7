package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExportToExcel extends Main {
    String filepath;

    public void exportData(String filepath) {
        this.filepath = filepath;

        try (PreparedStatement pst = con.prepareStatement(selectFromTable); ResultSet rs = pst.executeQuery()) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("task 7");
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue(rs.getMetaData().getColumnName(1));
            row.createCell(1).setCellValue(rs.getMetaData().getColumnName(2));
            row.createCell(2).setCellValue(rs.getMetaData().getColumnName(3));

            int rowIndex = 1;
            while (rs.next()) {
                Row row1 = sheet.createRow(rowIndex++);
                row1.createCell(0).setCellValue(rs.getInt(1));
                row1.createCell(1).setCellValue(rs.getArray(2).toString());
                if (rs.getArray(3) == null) {
                    row1.createCell(2).setCellValue("null");
                } else {
                    row1.createCell(2).setCellValue(rs.getArray(3).toString());
                }
            }
            int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
            try (FileOutputStream fos = new FileOutputStream(filepath)) {
                wb.write(fos);
            } catch (IOException e) {
                System.out.println("Ошибка при записи Excel-файла: " + e);
            } finally {
                wb.close();
                System.out.println("Данные успешно сохранены в Excel-файл: " + filepath);
            }
        } catch (IOException | SQLException e) {
            System.out.println("Ошибка при экспорте данных: " + e);
        }
    }

    public void printExcelData(String filepath) {
        try (Workbook wb = new XSSFWorkbook(filepath)) {
            Sheet sheet = wb.getSheetAt(0);
            System.out.println("\nДанные из Excel-файла:");

            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING -> System.out.print(cell.getStringCellValue() + "\t");
                        case NUMERIC -> System.out.print((int)cell.getNumericCellValue() + "\t");
                        default -> System.out.print(" \t");
                    }
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении Excel-файла: " + e.getMessage());
        }
    }
}
