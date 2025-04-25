package com.str.billing.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.str.billing.model.InvoiceItem;

import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {

    public static void generateInvoicePDF(int invoiceId, String customerName, String customerAddress, String gstin, String date,
                                          List<InvoiceItem> items, double sgstRate, double cgstRate, double igstRate,
                                          String filePath, String amountInWord) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font boldFont10 = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font regular = new Font(Font.HELVETICA, 10);
            Font headerFont = new Font(Font.HELVETICA, 18, Font.BOLD);

            // Company Header
            Paragraph companyInfo = new Paragraph("SHARVESH TOOL ROOM\n", headerFont);
            companyInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(companyInfo);

            Paragraph companyDetails = new Paragraph("32, Automobile co-oprative SIDCO industrial estate, Kappalur, Madurai - 625 008\n"
            		+ "cell: +91 88706 49367  |  E-mail: sharveshtoolroom@gmail.com", new Font(Font.HELVETICA, 10));
            companyDetails.setAlignment(Element.ALIGN_CENTER);
            document.add(companyDetails);
            
            Paragraph companyDetails1 = new Paragraph("GSTIN: 33FRDPB6014C1ZK", new Font(Font.HELVETICA, 10, Font.BOLD));
            companyDetails1.setAlignment(Element.ALIGN_CENTER);
            document.add(companyDetails1);
            document.add(new Paragraph("\n"));

            Paragraph title = new Paragraph("TAX INVOICE", new Font(Font.HELVETICA, 14, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
         // Customer Details and Invoice Details
            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);
            customerTable.setSpacingBefore(10);
            customerTable.setWidths(new float[]{1, 1}); // Ensures equal column width

            // First Row: Bill No (Left) & Order Date (Right)
            PdfPCell billNoCell = getCell("Invoice No: " + invoiceId, PdfPCell.ALIGN_LEFT, boldFont);
            PdfPCell orderDateCell = getCell("Date: " + date, PdfPCell.ALIGN_RIGHT, boldFont);
            billNoCell.setBorder(PdfPCell.NO_BORDER);
            orderDateCell.setBorder(PdfPCell.NO_BORDER);
            customerTable.addCell(billNoCell);
            customerTable.addCell(orderDateCell);

            // Second Row: Customer Name (Full Width)
            PdfPCell customerNameCell = new PdfPCell(new Phrase("Customer Name: " + customerName.toUpperCase(), boldFont10));
            customerNameCell.setColspan(2); // Full width
            customerNameCell.setPadding(5);
            customerNameCell.setBorder(PdfPCell.NO_BORDER);
            customerTable.addCell(customerNameCell);

            // Third Row: Address (Full Width)
            PdfPCell addressCell = new PdfPCell(new Phrase("Address: " + customerAddress.toUpperCase(), boldFont10));
            addressCell.setColspan(2); // Full width
            addressCell.setPadding(5);
            addressCell.setBorder(PdfPCell.NO_BORDER);
            customerTable.addCell(addressCell);

            // Fourth Row: GSTIN (Full Width)
            PdfPCell gstCell = new PdfPCell(new Phrase("GSTIN: " + gstin.toUpperCase(), boldFont10)); // Use correct GST variable
            gstCell.setColspan(2); // Full width
            gstCell.setPadding(5);
            gstCell.setBorder(PdfPCell.NO_BORDER);
            customerTable.addCell(gstCell);

            // Add table to document
            document.add(customerTable);


            // Item Table
            PdfPTable itemTable = new PdfPTable(6);
            itemTable.setWidthPercentage(100);
            itemTable.setSpacingBefore(10);
            itemTable.setSpacingAfter(10);
            
            // Define column widths: S.No (small), Description (large), Qty (small), others (equal)
            float[] columnWidths = {2f, 8f, 3f, 3f, 3f, 4f};
            itemTable.setWidths(columnWidths);
            
            itemTable.addCell(getCell("S.No", PdfPCell.ALIGN_CENTER, boldFont));
            itemTable.addCell(getCell("Description", PdfPCell.ALIGN_CENTER, boldFont));
            itemTable.addCell(getCell("HSN/SAC", PdfCell.ALIGN_CENTER, boldFont));
            itemTable.addCell(getCell("Qty", PdfPCell.ALIGN_CENTER, boldFont));
            itemTable.addCell(getCell("Rate", PdfPCell.ALIGN_CENTER, boldFont));
            itemTable.addCell(getCell("Amount", PdfPCell.ALIGN_CENTER, boldFont));

            double totalAmount = 0;
            int count = 1;
            int totalQuantity = 0;
            for (InvoiceItem item : items) {
                itemTable.addCell(getCell(String.valueOf(count++), PdfPCell.ALIGN_CENTER));
                itemTable.addCell(getCell(item.getDescription().toUpperCase(), PdfPCell.ALIGN_LEFT));
                itemTable.addCell(getCell(String.valueOf(item.getHsn()), PdfCell.ALIGN_CENTER));
                itemTable.addCell(getCell(String.valueOf(item.getQuantity()), PdfPCell.ALIGN_CENTER));
                itemTable.addCell(getCell(String.format("%.2f", item.getUnitPrice()), PdfPCell.ALIGN_CENTER));
                itemTable.addCell(getCell(String.format("%.2f", item.getAmount()), PdfPCell.ALIGN_CENTER));
                totalAmount += item.getAmount();
                totalQuantity += item.getQuantity();
            }
            // Add Total Row
            PdfPCell totalCell = new PdfPCell(new Phrase("Total ", boldFont));
            totalCell.setColspan(3);  // Span across S.No and Description
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemTable.addCell(totalCell);

            itemTable.addCell(getCell(String.valueOf(totalQuantity), PdfPCell.ALIGN_CENTER)); // Total Quantity
            itemTable.addCell(getCell("", PdfPCell.ALIGN_CENTER)); // Empty for Rate column
            itemTable.addCell(getCell(String.format("%.2f", totalAmount), PdfPCell.ALIGN_CENTER)); // Total Amount
            document.add(itemTable);
            //document.add(new Paragraph("\n"));

            // Tax and Total Amount
            double cgstAmount = (totalAmount * cgstRate) / 100;
            double sgstAmount = (totalAmount * sgstRate) / 100;
            double igstAmount = (totalAmount * igstRate) / 100;
            double netAmount = totalAmount + cgstAmount + sgstAmount + igstAmount;

            PdfPTable taxTable = new PdfPTable(5);
            taxTable.setWidthPercentage(100);
            taxTable.setSpacingBefore(10);
            taxTable.addCell(getCell("Taxable Value", PdfPCell.ALIGN_CENTER, boldFont));
            taxTable.addCell(getCell("CGST " + cgstRate + "%", PdfPCell.ALIGN_CENTER, boldFont));
            taxTable.addCell(getCell("SGST " + sgstRate + "%", PdfPCell.ALIGN_CENTER, boldFont));
            taxTable.addCell(getCell("IGST " + igstRate + "%", PdfPCell.ALIGN_CENTER, boldFont));
            taxTable.addCell(getCell("Total Amount", PdfPCell.ALIGN_CENTER, boldFont));

            taxTable.addCell(getCell(String.format("%.2f", totalAmount), PdfPCell.ALIGN_CENTER));
            taxTable.addCell(getCell(String.format("%.2f", cgstAmount), PdfPCell.ALIGN_CENTER));
            taxTable.addCell(getCell(String.format("%.2f", sgstAmount), PdfPCell.ALIGN_CENTER));
            taxTable.addCell(getCell(String.format("%.2f", igstAmount), PdfPCell.ALIGN_CENTER));
            taxTable.addCell(getCell(String.format("%.2f", netAmount), PdfPCell.ALIGN_CENTER));
            document.add(taxTable);

            document.add(new Paragraph("\n"));

            // Amount in Words
            String roundOff = String.format("%.0f", netAmount);
            Paragraph grandtotal = new Paragraph("Total Amount: " + roundOff + "/-" , boldFont);
            document.add(grandtotal);
            Paragraph amountInWords = new Paragraph(amountInWord, boldFont);
            document.add(amountInWords);
            document.add(new Paragraph("\n"));

            // Bank Details and Signature
            Paragraph bankDetails = new Paragraph("BANK DETAILS\n" +
                    "Bank Name: Axis Bank\n" +
            		"A/c Holder: SHARVESH TOOL ROOM\n" +
                    "A/C No: 921020036407257\n" +
                    "IFSC Code: UTIB0003136\n" +
                    "Branch: Thirunagar", new Font(Font.HELVETICA, 10));
            document.add(bankDetails);

            Paragraph signature = new Paragraph("For SHARVESH TOOL ROOM\n", boldFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);
            
            //for extra line space
            Paragraph signature1 = new Paragraph("\n", regular);
            signature1.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature1);
            
            //Table for signatures
            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setSpacingBefore(10);
            signTable.setWidths(new float[]{1, 1}); // Ensures equal column width

            // One row left side and right side signatures
            PdfPCell receverCell = getCell("Receiver's Signature", PdfPCell.ALIGN_LEFT, regular);
            PdfPCell autorityCell = getCell("Authorized Signatory", PdfPCell.ALIGN_RIGHT, regular);
            receverCell.setBorder(PdfPCell.NO_BORDER);
            autorityCell.setBorder(PdfPCell.NO_BORDER);
            autorityCell.setPaddingRight(30);
            signTable.addCell(receverCell);
            signTable.addCell(autorityCell);
            document.add(signTable);
         
            Paragraph thankYouNote = new Paragraph("Thank you for your business!", new Font(Font.HELVETICA,12, Font.ITALIC));
            thankYouNote.setAlignment(Element.ALIGN_CENTER);
            thankYouNote.setSpacingBefore(20);
            document.add(thankYouNote);
            
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PdfPCell getCell(String text, int alignment) {
        return getCell(text, alignment, new Font(Font.HELVETICA, 10));
    }

    private static PdfPCell getCell(String text, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }
    
}
