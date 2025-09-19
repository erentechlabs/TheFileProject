package com.thefileproject.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.ss.usermodel.*;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
@Service
public class PdfService {


    public byte[] convertTxtToPdf(MultipartFile file) {
        try {
            return textToPdf(new String(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("TXT to PDF conversion failed: " + e.getMessage(), e);
        }
    }


    public byte[] convertDocxToPdf(MultipartFile file) {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, outputStream, options);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("DOCX to PDF conversion failed: " + e.getMessage(), e);
        }
    }


    public byte[] convertXlsxToPdf(MultipartFile file) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            return convertWorkbookToPdf(workbook);
        } catch (IOException e) {
            throw new RuntimeException("XLSX to PDF conversion failed: " + e.getMessage(), e);
        }
    }


    public byte[] convertXlsToPdf(MultipartFile file) {
        try (HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream())) {
            return convertWorkbookToPdf(workbook);
        } catch (IOException e) {
            throw new RuntimeException("XLS to PDF conversion failed: " + e.getMessage(), e);
        }
    }


    public byte[] convertPdfToPdf(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("PDF to PDF copy failed: " + e.getMessage(), e);
        }
    }


    private byte[] textToPdf(String text) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(50, 750);

                for (String line : text.split("\n")) {
                    cs.showText(line);
                    cs.newLineAtOffset(0, -15);
                }

                cs.endText();
            }

            document.save(output);
            return output.toByteArray();
        }
    }

    private byte[] convertWorkbookToPdf(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDDocument pdfDoc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            pdfDoc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(pdfDoc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(50, 750);

                Sheet sheet = workbook.getSheetAt(0); // sadece ilk sayfa
                int lastRowNum = Math.min(sheet.getLastRowNum(), 100);

                for (int i = 0; i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < row.getLastCellNum() && j < 10; j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            line.append(getCellValueAsString(cell)).append("\t");
                        }
                    }
                    cs.showText(line.toString());
                    cs.newLineAtOffset(0, -15);
                }

                cs.endText();
            }

            pdfDoc.save(outputStream);
            pdfDoc.close();
            return outputStream.toByteArray();
        }
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
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

    public String getPdfContentType() {
        return MediaType.APPLICATION_PDF_VALUE;
    }
}
