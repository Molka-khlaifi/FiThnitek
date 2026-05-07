package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import models.Revenue;
import models.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class exportService {

    // ── Export Transactions to PDF ──────────────────────────
    public void exportTransactionsToPDF(List<Transaction> transactions, String path) {
        try {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();

            // Title
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 16,
                    com.itextpdf.text.Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Fi Thniytek — Transactions Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            doc.add(title);

            // Table
            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // Headers
            String[] headers = {"ID", "User ID", "Trip ID", "Montant", "Commission",
                    "Net", "Date", "Method", "Ref", "Status"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h,
                        new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                                10, com.itextpdf.text.Font.BOLD, BaseColor.WHITE)));
                cell.setBackgroundColor(new BaseColor(52, 152, 219));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            // Rows
            for (Transaction t : transactions) {
                table.addCell(String.valueOf(t.getId()));
                table.addCell(String.valueOf(t.getUserId()));
                table.addCell(String.valueOf(t.getTripId()));
                table.addCell(String.valueOf(t.getMontant()));
                table.addCell(String.valueOf(t.getCommissionPlatform()));
                table.addCell(String.valueOf(t.getMontantNet()));
                table.addCell(t.getDateTransaction());
                table.addCell(t.getMethodePaiement());
                table.addCell(t.getPaymentRef());
                table.addCell(t.getStatut());
            }

            doc.add(table);
            doc.close();
            System.out.println("Transactions PDF exported to: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Export Revenues to PDF ──────────────────────────────
    public void exportRevenuesToPDF(List<Revenue> revenues, String path) {
        try {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();

            // Title
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 16,
                    com.itextpdf.text.Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Fi Thniytek — Revenues Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            doc.add(title);

            // Table
            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // Headers
            String[] headers = {"ID", "Trans. ID", "User ID", "Type", "Montant",
                    "Date", "Rev. Type", "Mois", "Passagers", "Statut"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h,
                        new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA,
                                10, com.itextpdf.text.Font.BOLD, BaseColor.WHITE)));
                cell.setBackgroundColor(new BaseColor(52, 152, 219));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            // Rows
            for (Revenue r : revenues) {
                table.addCell(String.valueOf(r.getId()));
                table.addCell(String.valueOf(r.getTransactionId()));
                table.addCell(String.valueOf(r.getUserId()));
                table.addCell(r.getUserType());
                table.addCell(String.valueOf(r.getMontant()));
                table.addCell(r.getDateRevenue());
                table.addCell(r.getTypeRevenue());
                table.addCell(r.getMois());
                table.addCell(String.valueOf(r.getNbPassagers()));
                table.addCell(r.getStatut());
            }

            doc.add(table);
            doc.close();
            System.out.println("Revenues PDF exported to: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Export Transactions to Excel ────────────────────────
    public void exportTransactionsToExcel(List<Transaction> transactions, String path) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "User ID", "Trip ID", "Montant", "Commission",
                    "Net", "Date", "Method", "Ref", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // Rows
            int rowNum = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getId());
                row.createCell(1).setCellValue(t.getUserId());
                row.createCell(2).setCellValue(t.getTripId());
                row.createCell(3).setCellValue(t.getMontant());
                row.createCell(4).setCellValue(t.getCommissionPlatform());
                row.createCell(5).setCellValue(t.getMontantNet());
                row.createCell(6).setCellValue(t.getDateTransaction());
                row.createCell(7).setCellValue(t.getMethodePaiement());
                row.createCell(8).setCellValue(t.getPaymentRef());
                row.createCell(9).setCellValue(t.getStatut());
            }

            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fos = new FileOutputStream(path);
            workbook.write(fos);
            fos.close();
            System.out.println("Transactions Excel exported to: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Export Revenues to Excel ────────────────────────────
    public void exportRevenuesToExcel(List<Revenue> revenues, String path) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Revenues");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Trans. ID", "User ID", "Type", "Montant",
                    "Date", "Rev. Type", "Mois", "Passagers", "Statut"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Rows
            int rowNum = 1;
            for (Revenue r : revenues) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getId());
                row.createCell(1).setCellValue(r.getTransactionId());
                row.createCell(2).setCellValue(r.getUserId());
                row.createCell(3).setCellValue(r.getUserType());
                row.createCell(4).setCellValue(r.getMontant());
                row.createCell(5).setCellValue(r.getDateRevenue());
                row.createCell(6).setCellValue(r.getTypeRevenue());
                row.createCell(7).setCellValue(r.getMois());
                row.createCell(8).setCellValue(r.getNbPassagers());
                row.createCell(9).setCellValue(r.getStatut());
            }

            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fos = new FileOutputStream(path);
            workbook.write(fos);
            fos.close();
            System.out.println("Revenues Excel exported to: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}