package com.thefileproject.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
@Service
public class OfficeService {


    public byte[] convertPdfToDocx(MultipartFile file) {
        try (PDDocument pdf = Loader.loadPDF(file.getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream();
             XWPFDocument docx = new XWPFDocument()) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdf);

            for (String line : text.split("\n")) {
                var p = docx.createParagraph();
                var run = p.createRun();
                run.setText(line);
            }

            docx.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("PDF to DOCX conversion failed: " + e.getMessage(), e);
        }
    }



    public byte[] convertDocxToXlsx(MultipartFile file) {
        try (XWPFDocument docx = new XWPFDocument(file.getInputStream());
             ByteArrayOutputStream output = new ByteArrayOutputStream();
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("From DOCX");

            int rowIndex = 0;
            for (var para : docx.getParagraphs()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(para.getText());
            }

            workbook.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("DOCX to XLSX conversion failed: " + e.getMessage(), e);
        }
    }


    public byte[] convertXlsxToDocx(MultipartFile file) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
             ByteArrayOutputStream output = new ByteArrayOutputStream();
             XWPFDocument doc = new XWPFDocument()) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                var p = doc.createParagraph();
                var run = p.createRun();

                StringBuilder line = new StringBuilder();
                for (Cell cell : row) {
                    line.append(getCellValueAsString(cell)).append("\t");
                }
                run.setText(line.toString().trim());
            }

            doc.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("XLSX to DOCX conversion failed: " + e.getMessage(), e);
        }
    }


    public byte[] convertDocxToDocx(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("DOCX copy failed: " + e.getMessage(), e);
        }
    }


    public byte[] convertXlsxToXlsx(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("XLSX copy failed: " + e.getMessage(), e);
        }
    }


    private String getCellValueAsString(Cell cell) {
        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default -> "";
            };
        } catch (Exception e) {
            return "";
        }
    }

    public String buildOutputFileName(String originalFilename, String targetExtension) {
        String base = originalFilename;
        int idx = originalFilename.lastIndexOf('.');
        if (idx > 0) {
            base = originalFilename.substring(0, idx);
        }
        return base + "." + targetExtension;
    }
}
