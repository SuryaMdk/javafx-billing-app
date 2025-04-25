package com.str.billing.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import com.str.billing.dao.CustomerDAO;
import com.str.billing.dao.InvoiceDAO;
import com.str.billing.dao.InvoiceItemDAO;
import com.str.billing.model.Customer;
import com.str.billing.model.Invoice;
import com.str.billing.model.InvoiceItem;
import com.str.billing.util.InvoicePdfGeneration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;


public class InvoiceReportsController {

	@FXML private TableView<Invoice> invoiceTable;
	@FXML private TableView<InvoiceItem>  invoiceItemsTable;
	
    @FXML private ComboBox<String> comboCustomerName;
    private Map<String, Customer> customerMap;

	@FXML private TableColumn<Invoice, Void> colSno;
	@FXML private TableColumn<InvoiceItem, Void> colItemSno;
	
	@FXML private TableColumn<Invoice, Integer> colInvoiceId;
	@FXML private TableColumn<Invoice, String> colCustomerName, colOrderDate, colDeliveryDate, colCreatedAt, colUpdatedAt;
	@FXML private TableColumn<Invoice, Double> colTotalAmount, colGrandTotal, colSGST, colCGST, colIGST;

	@FXML private TableColumn<InvoiceItem, Integer> colItemInvoiceId, colItemId, colHSN, colQuantity;
	@FXML private TableColumn<InvoiceItem, String> colDescription;
	@FXML private TableColumn<InvoiceItem, Double> colUnitPrice, colTotalPrice;	
	
	@FXML private DatePicker dpFromDate, dpToDate;
	
	private final InvoiceDAO invoiceDAO = new InvoiceDAO();
	ObservableList<Invoice> allInvoices = FXCollections.observableArrayList();

	@FXML
	private void initialize() {
		//Load customer names from DB
    	customerMap = CustomerDAO.getAllCustomersMap();
    	ObservableList<String> names = FXCollections.observableArrayList(customerMap.keySet());
    	comboCustomerName.setItems(names.sorted());
		
		//initialize invoice table columns
		colInvoiceId.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
		colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
		colGrandTotal.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));
		colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		colDeliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
		colSGST.setCellValueFactory(new PropertyValueFactory<>("sgst"));
		colCGST.setCellValueFactory(new PropertyValueFactory<>("cgst"));
		colIGST.setCellValueFactory(new PropertyValueFactory<>("igst"));
		colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

		//set serial number to the valid rows in invoice table
		colSno.setCellFactory(col -> new TableCell<Invoice, Void>() {
		    @Override
		    protected void updateItem(Void item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(String.valueOf(getIndex() + 1));
		        }
		    }
		});

		//load data
		try {
			allInvoices.addAll(invoiceDAO.getAllInvoices());
			invoiceTable.setItems(allInvoices);
		}catch(Exception e) {
			e.printStackTrace();
		}
		//initialize invoice item table columns
		colItemInvoiceId.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
		colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
		colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		colHSN.setCellValueFactory(new PropertyValueFactory<>("hsn"));
		colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
		colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("amount"));
		
		//set serial number to the valid rows in invoice item table
		colItemSno.setCellFactory(col -> new TableCell<InvoiceItem, Void>() {
		    @Override
		    protected void updateItem(Void item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty) {
		            setText(null);
		        } else {
		            setText(String.valueOf(getIndex() + 1));
		        }
		    }
		});
		
		//load data in invoice item table
		invoiceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldInvoice, newInvoice) -> {
			if(newInvoice != null) {
				int invoiceId = newInvoice.getInvoiceId();	//get selected inovice's id
			
				List<InvoiceItem> items = InvoiceItemDAO.getItemsByInvoiceId(invoiceId); //get inovice list from dao by using invoice id
			
				ObservableList<InvoiceItem> itemList = FXCollections.observableArrayList(items); //convert to observalble list and set items into it
				invoiceItemsTable.setItems(itemList);
			}
		});
		
		
	}
	
	@FXML
	private void handleFilterInvoices() {
		String name = comboCustomerName.getValue();
		LocalDate fromDate = dpFromDate.getValue();
		LocalDate toDate = dpToDate.getValue();
		
		//validation
		if((name == null ||name.isEmpty()) && (fromDate == null || toDate == null)) {
			showAlert("Warning! ", "Please select customer or both From and To dates.");
			return;
		}
		//filter by customer only
		if(name != null && !name.isEmpty() && fromDate == null || toDate == null) {	
			ObservableList<Invoice> filteredList = allInvoices.filtered(invoice -> {
				return invoice.getCustomerName().equalsIgnoreCase(name);
			});
			invoiceTable.setItems(filteredList);
			return;
		}
		//filter by dates only
		if((name == null || name.isEmpty()) && fromDate != null && toDate != null) {
			ObservableList<Invoice> filteredList = allInvoices.filtered(invoice -> {
				LocalDate invoiceDate = invoice.getDeliveryDate();
				return (!invoiceDate.isBefore(fromDate) && !invoiceDate.isAfter(toDate));
			});
			invoiceTable.setItems(filteredList);
			return;
		}
		//filter by both customer and dates
		if(name != null && !name.isEmpty() && fromDate != null && toDate != null) {
			ObservableList<Invoice> filteredList = allInvoices.filtered(invoice -> {
				LocalDate invoiceDate = invoice.getDeliveryDate();
				return(invoice.getCustomerName().equalsIgnoreCase(name)
						&& !invoiceDate.isBefore(fromDate)
						&& !invoiceDate.isAfter(toDate));
			});
			invoiceTable.setItems(filteredList);
		}
	}
	
	@FXML
	private void handleResetFilter() {
		dpFromDate.setValue(null);
		dpToDate.setValue(null);
		comboCustomerName.setValue(null);
		invoiceTable.setItems(allInvoices);
		invoiceTable.refresh();
	}
	
	@FXML
	private void handleSaveInvoice() {
		Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
		
		if(selectedInvoice == null) {
			showAlert("Warning", "Please select an invoice to print");
			return;
		}
		
		try {
			//Getting invoice items from selected invoice
			List<InvoiceItem> items = InvoiceItemDAO.getItemsByInvoiceId(selectedInvoice.getInvoiceId());
			Customer customer = CustomerDAO.getCustomerById(selectedInvoice.getCustomerId());
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			
			//choose where to save
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Invoice");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
			File file = fileChooser.showSaveDialog(null);
			
			
			if(file != null) {
				//Generate the invoice pdf
				InvoicePdfGeneration.generateInvoicePDF(
						selectedInvoice.getInvoiceId(),
						customer.getName(),
						customer.getAddress(), 
						customer.getPhone(),
						customer.getGstNumber(), 
						selectedInvoice.getDeliveryDate().format(format), 
						items, 
						percent(selectedInvoice.getSgst(), selectedInvoice.getTotalAmount()), 
						percent(selectedInvoice.getCgst(), selectedInvoice.getTotalAmount()), 
						percent(selectedInvoice.getIgst(), selectedInvoice.getTotalAmount()), 
						file.getAbsolutePath(), 
						selectedInvoice.getAmountInWords()
				);
				showAlert("Success", "Invoice Saved Successfully");
			}
			
		}catch(Exception e) {
			System.out.println("Error in printing invoice: " + e.getMessage());
		}
	}
	
	@FXML
	private void handlePrintInvoice() {
		Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
		
		if(selectedInvoice == null) {
			showAlert("Warning", "Please select an invoice to print");
			return;
		}
		
		try {
			//Getting invoice items from selected invoice
			List<InvoiceItem> items = InvoiceItemDAO.getItemsByInvoiceId(selectedInvoice.getInvoiceId());
			Customer customer = CustomerDAO.getCustomerById(selectedInvoice.getCustomerId());
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		
			// Generate PDF to a temporary file
	        File tempFile = File.createTempFile("invoice_" + selectedInvoice.getInvoiceId(), ".pdf");
	        
			if(tempFile != null) {
				//Generate the invoice pdf
				InvoicePdfGeneration.generateInvoicePDF(
						selectedInvoice.getInvoiceId(),
						customer.getName(),
						customer.getAddress(), 
						customer.getPhone(),
						customer.getGstNumber(), 
						selectedInvoice.getDeliveryDate().format(format), 
						items, 
						percent(selectedInvoice.getSgst(), selectedInvoice.getTotalAmount()), 
						percent(selectedInvoice.getCgst(), selectedInvoice.getTotalAmount()), 
						percent(selectedInvoice.getIgst(), selectedInvoice.getTotalAmount()), 
						tempFile.getAbsolutePath(), 
						selectedInvoice.getAmountInWords()
				);
				
				try {
					 // Send PDF to printer
			        Desktop.getDesktop().print(tempFile);  // This opens default system print dialog
				}catch(Exception e) {
					System.out.println("Error printing pdf: " + e.getMessage());
					Desktop.getDesktop().open(tempFile); 	// Open file if any problem when print
				}
				System.out.println("Success" + "Invoice printed Successfully");
			}
			
		}catch(Exception e) {
			System.out.println("Error in printing invoice: " + e.getMessage());
		}
	}
	
	@FXML
	private void handleExportInvoices() {
		//choose file where to save
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Save Excel File");
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
	    File file = fileChooser.showSaveDialog(null);

	    if (file == null) return;

	    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
	        XSSFSheet sheet = workbook.createSheet("Invoices");

	        // Header Row
	        Row headerRow = sheet.createRow(0);
	        String[] headers = {"S.No", "Invoice ID", "Customer Name", "Order Date", "Delivery Date", "Total", "SGST", "CGST", "IGST", "Grand Total", "Amount In Words", "Created At", "Updated At"};
	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	        }

	        // Data Rows
	        ObservableList<Invoice> invoices = invoiceTable.getItems();
	        for (int i = 0; i < invoices.size(); i++) {
	            Invoice inv = invoices.get(i);
	            Row row = sheet.createRow(i + 1);

	            row.createCell(0).setCellValue(i + 1); // S.No
	            row.createCell(1).setCellValue(inv.getInvoiceId());
	            row.createCell(2).setCellValue(CustomerDAO.getCustomerNameById(inv.getCustomerId()));
	            row.createCell(3).setCellValue(inv.getOrderDate().toString());
	            row.createCell(4).setCellValue(inv.getDeliveryDate().toString());
	            row.createCell(5).setCellValue(inv.getTotalAmount());
	            row.createCell(6).setCellValue(inv.getSgst());
	            row.createCell(7).setCellValue(inv.getCgst());
	            row.createCell(8).setCellValue(inv.getIgst());
	            row.createCell(9).setCellValue(inv.getGrandTotal());
	            row.createCell(10).setCellValue(inv.getAmountInWords());
	            row.createCell(11).setCellValue(inv.getCreatedAt());
	            row.createCell(12).setCellValue(inv.getUpdatedAt());
	        }

	        // Autosize columns
	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        // Write to file
	        try (FileOutputStream fos = new FileOutputStream(file)) {
	            workbook.write(fos);
	        }

	        showAlert("Success", "Exported to Excel successfully!");

	    } catch (IOException e) {
	        e.printStackTrace();
	        showAlert("Error", "Failed to export to Excel.");
	    }
	}

	
	@FXML
	private void handleDeleteInvoice() {
		Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
		
		if(selectedInvoice == null) {
			showAlert("Warning", "Please select an invoice");
			return;
		}
		
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this invoice");
		alert.setContentText("Invoid ID: " + selectedInvoice.getInvoiceId());
		
		Optional<ButtonType> result = alert.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.OK) {
			try {
				boolean deleted = invoiceDAO.deleteInvoiceById(selectedInvoice.getInvoiceId());
				
				if(deleted) {
					invoiceTable.getItems().remove(selectedInvoice);
					showAlert("Success", "Invoice Deleted Successfully.");
				}else {
					showAlert("Error", "Failed to Delete Invoice.");
				}
			}catch(Exception e) {
				showAlert("Error", "An error occured while deleting invoice!");
				System.out.println("Error in delete invoice: " + e.getMessage());
			}
		}
	}
	
	private double percent(double part, double total) {
		return Math.round((part/total)*100);
	}
	
	private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
