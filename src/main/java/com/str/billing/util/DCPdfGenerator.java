package com.str.billing.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.str.billing.model.*;
import java.util.List;
import java.awt.Color;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

public class DCPdfGenerator {
	
    public static void generateDCPdf(DC dc, Customer customer, List<DCItem> items, String filePath) {
    	try {
    		Document document = new Document(PageSize.A4);
    		PdfWriter.getInstance(document, new FileOutputStream(filePath));
    		document.open();
    		
    		// Color palette
    		Color navyBlue = new Color(0x00, 0x2F, 0x6C);       // Header
    		Color redTitle = new Color(204, 0, 0);
    		Color altRow = new Color(0xF0, 0xF0, 0xF5);         // Alternate row
    		Color borderGray = new Color(0xCC, 0xCC, 0xCC);     // Table borders
    		Color textBlack = new Color(0x00, 0x00, 0x00);      // Text

    		// Fonts
    		BaseFont helveticaBase = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);

    		Font headerFont = new Font(helveticaBase, 20, Font.BOLD, navyBlue);
    		Font regularFont = new Font(Font.HELVETICA, 10, Font.NORMAL, textBlack);
    		Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
    		Font boldFont12 = new Font(Font.HELVETICA, 12, Font.BOLD);
    		Font boldFont1 = new Font(Font.HELVETICA, 12, Font.BOLD, navyBlue);
    		
    		// Company Header
    		Paragraph companyInfo = new Paragraph("SHARVESH TOOL ROOM\n", headerFont);
    		companyInfo.setAlignment(Element.ALIGN_CENTER);
    		document.add(companyInfo);
    		
    		Paragraph companyDetails = new Paragraph(
    		"32, Automobile co-oprative SIDCO industrial estate, Kappalur, Madurai - 625 008\n"
    		+ "cell: +91 88706 49367  |  E-mail: sharveshtoolroom@gmail.com", regularFont);
    		companyDetails.setAlignment(Element.ALIGN_CENTER);
    		document.add(companyDetails);
    		
    		Paragraph companyDetails1 = new Paragraph("GSTIN: 33FRDPB6014C1ZK", boldFont);
    		companyDetails1.setAlignment(Element.ALIGN_CENTER);
    		document.add(companyDetails1);
    		document.add(new Paragraph("\n"));
    		
    		Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD, redTitle);
    		Paragraph title = new Paragraph("DELIVERY CHALLAN", titleFont);
    		title.setAlignment(Element.ALIGN_CENTER);
    		document.add(title);
    		
    		// Customer Details Table
    		PdfPTable customerTable = new PdfPTable(2);
    		customerTable.setWidthPercentage(100);
    		customerTable.setSpacingBefore(10);
    		customerTable.setWidths(new float[]{1, 1});
    		
    		DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy");
    		PdfPCell billNoCell = getCell("DC No: " + dc.getDcId(), PdfPCell.ALIGN_LEFT, boldFont12);
    		PdfPCell dateCell = getCell("Date: " + dc.getDeliveryDate().format(formatter), PdfPCell.ALIGN_RIGHT, boldFont12);
    		billNoCell.setBorder(PdfPCell.NO_BORDER);
    		billNoCell.setPaddingLeft(3);
    		dateCell.setBorder(PdfPCell.NO_BORDER);
    		customerTable.addCell(billNoCell);
    		customerTable.addCell(dateCell);
    		
    		PdfPCell billTo = new PdfPCell(new Phrase("Bill To", boldFont));
    		billTo.setColspan(2);
    		billTo.setPaddingTop(10);
    		billTo.setPaddingLeft(3);
    		//billTo.setPaddingBottom(5);
    		billTo.setBorder(PdfPCell.NO_BORDER);
    		customerTable.addCell(billTo);
    		
    		PdfPCell customerNameCell = new PdfPCell(new Phrase(customer.getName(), boldFont1));
    		customerNameCell.setColspan(2);
    		customerNameCell.setPadding(3);
    		customerNameCell.setBorder(PdfPCell.NO_BORDER);
    		customerTable.addCell(customerNameCell);
    		
    		PdfPCell addressCell = new PdfPCell(new Phrase(customer.getAddress(), regularFont));
    		addressCell.setColspan(2);
    		addressCell.setPadding(3);
    		addressCell.setBorder(PdfPCell.NO_BORDER);
    		customerTable.addCell(addressCell);
    		
    		PdfPCell phoneCell = new PdfPCell(new Phrase(customer.getPhone(), regularFont));
    		phoneCell.setColspan(2);
    		phoneCell.setPadding(3);
    		phoneCell.setBorder(PdfPCell.NO_BORDER);
    		customerTable.addCell(phoneCell);
    		
    		PdfPCell gstCell = new PdfPCell(new Phrase(customer.getGstNumber(), boldFont));
    		gstCell.setColspan(2);
    		gstCell.setPadding(3);
    		gstCell.setBorder(PdfPCell.NO_BORDER);
    		customerTable.addCell(gstCell);
    		
    		document.add(customerTable);
    		
    		// Item Table
    		PdfPTable itemTable = new PdfPTable(4);
    		itemTable.setWidthPercentage(100);
    		itemTable.setSpacingBefore(10);
    		itemTable.setSpacingAfter(10);
    		itemTable.setWidths(new float[]{2f, 8f, 2f, 6f});
    		
    		//Table header
    		String[] headers = {"S.No", "Description", "Quantity", "Remarks"};
    		for (String h : headers) {
    		PdfPCell header = getCell(h, PdfPCell.ALIGN_CENTER, boldFont);
    		header.setBorderColor(borderGray);
    		itemTable.addCell(header);
    		}
    		
    		int count = 1;
    		int totalQuantity = 0;
    		
    		for (DCItem item : items) {
    		boolean isEven = count % 2 == 0;
    		Color bgColor = isEven ? altRow : Color.WHITE;
    		
    		PdfPCell[] rowCells = {
    		getCell(String.valueOf(count), PdfPCell.ALIGN_CENTER),
    		getCell(item.getDescription().toUpperCase(), PdfPCell.ALIGN_LEFT),
    		getCell(String.valueOf(item.getQuantity()), PdfPCell.ALIGN_CENTER),
    		getCell(String.format(item.getRemarks()), PdfPCell.ALIGN_CENTER)
    		};
    		
    		for (PdfPCell c : rowCells) {
    		c.setBackgroundColor(bgColor);
    		c.setBorderColor(borderGray);
    		itemTable.addCell(c);
    		}
    		
    		totalQuantity += item.getQuantity();
    		count++;
    		}
    		
    		//Total row
    		PdfPCell totalCell = new PdfPCell(new Phrase("Total ", boldFont));
    		totalCell.setColspan(2);
    		totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		totalCell.setBorderColor(borderGray);
    		totalCell.setPaddingTop(5);
    		totalCell.setPaddingRight(10);
    		itemTable.addCell(totalCell);
    		
    		PdfPCell qutyCell = getCell(String.valueOf(totalQuantity), PdfPCell.ALIGN_CENTER, boldFont);
    		PdfPCell rateCell = getCell("", PdfPCell.ALIGN_CENTER);
    		PdfPCell totalAmountCell = getCell("", PdfPCell.ALIGN_CENTER, boldFont);
    		
    		qutyCell.setBorderColor(borderGray);
    		rateCell.setBorderColor(borderGray);
    		totalAmountCell.setBorderColor(borderGray);

    		itemTable.addCell(qutyCell);
    		itemTable.addCell(rateCell);
    		itemTable.addCell(totalAmountCell);
    		document.add(itemTable);
    		
    		//Empty row space after table
    		document.add(new Paragraph("\n"));

    		Paragraph signature = new Paragraph("For SHARVESH TOOL ROOM\n", new Font(helveticaBase, 12, Font.BOLD));
    		signature.setAlignment(Element.ALIGN_RIGHT);
    		document.add(signature);
    		document.add(new Paragraph("\n"));
    		
    		// Signature Table
    		PdfPTable signTable = new PdfPTable(2);
    		signTable.setWidthPercentage(100);
    		signTable.setSpacingBefore(10);
    		signTable.setWidths(new float[]{1, 1});
    		
    		PdfPCell receiver = getCell("Receiver's Signature", PdfPCell.ALIGN_LEFT, regularFont);
    		PdfPCell authority = getCell("Authorized Signatory", PdfPCell.ALIGN_RIGHT, regularFont);
    		receiver.setBorder(PdfPCell.NO_BORDER);
    		authority.setBorder(PdfPCell.NO_BORDER);
    		authority.setPaddingRight(30);
    		signTable.addCell(receiver);
    		signTable.addCell(authority);
    		document.add(signTable);
    		
    		Paragraph thankYouNote = new Paragraph("Thanks for your business!", new Font(Font.HELVETICA, 10, Font.ITALIC));
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
    	    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
    	    return cell;
    	}

}
