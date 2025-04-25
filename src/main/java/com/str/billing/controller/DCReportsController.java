package com.str.billing.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.str.billing.dao.CustomerDAO;
import com.str.billing.dao.DCDAO;
import com.str.billing.dao.DCItemDAO;
import com.str.billing.model.*;
import com.str.billing.util.DCPdfGenerator;

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

public class DCReportsController {

	@FXML private TableView<DC> dcTable;
	@FXML private TableView<DCItem> dcItemsTable;
	
    @FXML private ComboBox<String> comboCustomerName;
    private Map<String, Customer> customerMap;

	@FXML private TableColumn<DC, Void> colSno;
	@FXML private TableColumn<DCItem, Void> colItemSno;
	
	@FXML private TableColumn<DC, Integer> colDCId;
	@FXML private TableColumn<DC, String> colCustomerName, colOrderDate, colDeliveryDate, colCreatedAt;

	@FXML private TableColumn<DCItem, Integer> colItemDCId, colItemId, colQuantity;
	@FXML private TableColumn<DCItem, String> colDescription, colRemarks;
	
	@FXML private DatePicker dpFromDate, dpToDate;
	
	ObservableList<DC> allDCs = FXCollections.observableArrayList();

	@FXML
	private void initialize() {
		//Load customer names from DB
    	customerMap = CustomerDAO.getAllCustomersMap();
    	ObservableList<String> names = FXCollections.observableArrayList(customerMap.keySet());
    	comboCustomerName.setItems(names.sorted());
		
		//initialize DC table columns
		colDCId.setCellValueFactory(new PropertyValueFactory<>("dcId"));
		colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
		colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		colDeliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
		colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

		//set serial number to the valid rows in DC table
		colSno.setCellFactory(col -> new TableCell<DC, Void>() {
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
			allDCs.addAll(DCDAO.getAllDCs());
			dcTable.setItems(allDCs);
		}catch(Exception e) {
			e.printStackTrace();
		}
		//initialize DC item table columns
		colItemDCId.setCellValueFactory(new PropertyValueFactory<>("dcId"));
		colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
		colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
		
		//set serial number to the valid rows in DC item table
		colItemSno.setCellFactory(col -> new TableCell<DCItem, Void>() {
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
		
		//load data in DC item table
		dcTable.getSelectionModel().selectedItemProperty().addListener((obs, oldDC, newDC) -> {
			if(newDC != null) {
				int dcId = newDC.getDcId();	//get selected dc's id
			
				//get dc list from dao by using dc id
				List<DCItem> items = DCItemDAO.getItemsByDCId(dcId); 
			
				//convert to observalble list and set items into it
				ObservableList<DCItem> itemList = FXCollections.observableArrayList(items); 
				dcItemsTable.setItems(itemList);
			}
		});
		
		
	}
	
	@FXML
	private void handleFilterDCs() {
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
			ObservableList<DC> filteredList = allDCs.filtered(dc -> {
				return dc.getCustomerName().equalsIgnoreCase(name);
			});
			dcTable.setItems(filteredList);
			return;
		}
		//filter by dates only
		if((name == null || name.isEmpty()) && fromDate != null && toDate != null) {
			ObservableList<DC> filteredList = allDCs.filtered(dc -> {
				LocalDate dcDate = dc.getDeliveryDate();
				return (!dcDate.isBefore(fromDate) && !dcDate.isAfter(toDate));
			});
			dcTable.setItems(filteredList);
			return;
		}
		//filter by both customer and dates
		if(name != null && !name.isEmpty() && fromDate != null && toDate != null) {
			ObservableList<DC> filteredList = allDCs.filtered(dc -> {
				LocalDate dcDate = dc.getDeliveryDate();
				return(dc.getCustomerName().equalsIgnoreCase(name)
						&& !dcDate.isBefore(fromDate)
						&& !dcDate.isAfter(toDate));
			});
			dcTable.setItems(filteredList);
		}
	}
	
	@FXML
	private void handleResetFilter() {
		dpFromDate.setValue(null);
		dpToDate.setValue(null);
		comboCustomerName.setValue(null);
		dcTable.setItems(allDCs);
		dcTable.refresh();
	}
	
	@FXML
	private void handleSaveDC() {
		DC selectedDC = dcTable.getSelectionModel().getSelectedItem();
		
		if(selectedDC == null) {
			showAlert("Warning", "Please select an dc to print");
			return;
		}
		
		try {
			//Getting DC items from selected invoice
			List<DCItem> items = DCItemDAO.getItemsByDCId(selectedDC.getDcId());
			Customer customer = CustomerDAO.getCustomerById(selectedDC.getCustomerId());
			
			//choose where to save
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save DC");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
			File file = fileChooser.showSaveDialog(null);
			
			
			if(file != null) {
				//Generate the invoice pdf
				DCPdfGenerator.generateDCPdf(selectedDC, customer, items, file.getAbsolutePath());
				showAlert("Success", "DC Saved Successfully");
			}
			
		}catch(Exception e) {
			System.out.println("Error in saving DC: " + e.getMessage());
		}
	}
	
	@FXML
	private void handlePrintDC() {
		DC selectedDC = dcTable.getSelectionModel().getSelectedItem();
		
		if(selectedDC == null) {
			showAlert("Warning", "Please select an DC to print");
			return;
		}
		
		try {
			//Getting invoice items from selected invoice
			List<DCItem> items = DCItemDAO.getItemsByDCId(selectedDC.getDcId());
			Customer customer = CustomerDAO.getCustomerById(selectedDC.getCustomerId());
		
			// Generate PDF to a temporary file
	        File tempFile = File.createTempFile("dc_" + selectedDC.getDcId(), ".pdf");
	        
	        if(tempFile != null) {
				//Generate the invoice pdf
				DCPdfGenerator.generateDCPdf(selectedDC, customer, items, tempFile.getAbsolutePath());
			
				try {
					 // Send PDF to printer
			        Desktop.getDesktop().print(tempFile);  // This opens default system print dialog
				}catch(Exception e) {
					System.out.println("Error printing pdf: " + e.getMessage());
					Desktop.getDesktop().open(tempFile); 	// Open file if any problem when print
				}
				System.out.println("Success" + "DC printed Successfully");
			}
			
		}catch(Exception e) {
			System.out.println("Error in printing DC: " + e.getMessage());
		}
	}
	
	@FXML
	private void handleExportDCs() {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Save Excel File");
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
	    File file = fileChooser.showSaveDialog(null);

	    if (file == null) return;

	    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
	        XSSFSheet sheet = workbook.createSheet("Delivery Challans");

	        // Header Row
	        Row headerRow = sheet.createRow(0);
	        String[] headers = {"S.No", "DC ID", "Customer Name", "Order Date", "Delivery Date", "Created At"};
	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	        }

	        // Data Rows
	        ObservableList<DC> DCs = dcTable.getItems();
	        for (int i = 0; i < DCs.size(); i++) {
	            DC tempDC = DCs.get(i);
	            Row row = sheet.createRow(i + 1);

	            row.createCell(0).setCellValue(i + 1); // S.No
	            row.createCell(1).setCellValue(tempDC.getDcId());
	            row.createCell(2).setCellValue(CustomerDAO.getCustomerNameById(tempDC.getCustomerId()));
	            row.createCell(3).setCellValue(tempDC.getOrderDate().toString());
	            row.createCell(4).setCellValue(tempDC.getDeliveryDate().toString());
	            row.createCell(5).setCellValue(tempDC.getCreatedAt());
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
	private void handleDeleteDC() {
		DC selectedDC = dcTable.getSelectionModel().getSelectedItem();
		
		if(selectedDC == null) {
			showAlert("Warning", "Please select an invoice");
			return;
		}
		
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Confirmation");
		alert.setHeaderText("Are you sure you want to delete this DC");
		alert.setContentText("DC ID: " + selectedDC.getDcId());
		
		Optional<ButtonType> result = alert.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.OK) {
			try {
				boolean deleted = DCDAO.deleteDCById(selectedDC.getDcId());
				
				if(deleted) {
					dcTable.getItems().remove(selectedDC);
					showAlert("Success", "DC Deleted Successfully.");
				}else {
					showAlert("Error", "Failed to Delete DC.");
				}
			}catch(Exception e) {
				showAlert("Error", "An error occured while deleting DC!");
				System.out.println("Error in delete DC: " + e.getMessage());
			}
		}
	}
	
	private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
