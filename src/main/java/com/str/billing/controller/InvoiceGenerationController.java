package com.str.billing.controller;

import com.str.billing.dao.*;
import com.str.billing.model.*;
import com.str.billing.services.*;
import com.str.billing.util.InvoicePdfGeneration;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class InvoiceGenerationController {

    @FXML private TextField txtInvoiceId, txtCustomerAddress, txtGstin, txtSerialNo, txtDescription, txtHsn, txtQuantity, txtUnitPrice;
    @FXML private DatePicker dpOrderDate,dpDeliveryDate;
    @FXML private ComboBox<String> comboCustomerName;
    
    @FXML private TableView<InvoiceItem> invoiceItemTable;
    @FXML private TableColumn<InvoiceItem, Integer> colSerialNo, colHsn, colQuantity;
    @FXML private TableColumn<InvoiceItem, String> colDescription;
    @FXML private TableColumn<InvoiceItem, Double> colUnitPrice, colAmount;
    
    @FXML private TextField txtCGST, txtSGST, txtIGST;
    @FXML private Label lblTotal, lblCGST, lblSGST, lblIGST, lblTotalAmount, lblAmountInWords;

    private Map<String, Customer> customerMap;
    private ObservableList<InvoiceItem> invoiceItems = FXCollections.observableArrayList();
    private final InvoiceService invoiceService = new InvoiceService();
    
    private int sno = 0;
    private int customerId;
    private double total, sgst, cgst, igst, finalAmount;
    private String amountInWords;


    @FXML
    public void initialize() {
    	//Load customer names from DB
    	customerMap = CustomerDAO.getAllCustomersMap();
    	ObservableList<String> names = FXCollections.observableArrayList(customerMap.keySet());
    	comboCustomerName.setItems(names.sorted());
        
        //Auto fill customer address
        comboCustomerName.valueProperty().addListener((obs, oldVal, newVal)-> {
        	Customer selected = customerMap.get(newVal);
        	if(selected != null) {
        		txtCustomerAddress.setText(selected.getAddress());
        		txtGstin.setText(selected.getGstNumber());
        		customerId = selected.getCustomerId();
        	}
        });
        
    	txtCGST.setText("6");  // Set default CGST value
        txtSGST.setText("6");  // Set default SGST value
        colSerialNo.setCellValueFactory(cellData -> cellData.getValue().itemIdProperty().asObject());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colHsn.setCellValueFactory(cellData -> cellData.getValue().hsnProperty().asObject());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        colUnitPrice.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty().asObject());
        colAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        invoiceItemTable.setItems(invoiceItems);
    }

    @FXML
    private void handleAddItem() {
        try {
            int invoiceId =  Integer.parseInt(txtInvoiceId.getText().trim());
            String description = txtDescription.getText().trim().toUpperCase();
            int hsn = Integer.parseInt(txtHsn.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            double unitPrice = Double.parseDouble(txtUnitPrice.getText().trim());

            if(invoiceService.isValidInvoiceId(invoiceId)) {
            	showAlert("Invalid input", "Invoice id already exist.");
            	return;
            }
            if(!invoiceService.isValidHsn(hsn)){
            	showAlert("Invalid input", "Enter valid HSN/SAC value");
            	return;
            }
            if(description.isEmpty()) {
            	showAlert("Invalid input", "Please Enter Description");
            	return;
            }
            if(quantity <= 0) {
            	showAlert("Invalid input", "Enter valid quantity.");
            	return;
            }
            if(unitPrice <= 0) {
            	showAlert("Invalid input", "Enter valid unitPrice.");
            	return;
            }
            
            int itemId = ++sno;  
            InvoiceItem item = new InvoiceItem(itemId, invoiceId, description, hsn, quantity, unitPrice);
            
            invoiceItems.add(item);
            invoiceItemTable.refresh();
            updateInvoiceSummary();
            clearItemFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numerical values before adding item.");
        }
    }

    private void updateInvoiceSummary() {
        total = invoiceItems.stream().mapToDouble(InvoiceItem::getAmount).sum();
        
        double cgstRate = parseDouble(txtCGST.getText(), 0.0);
        double sgstRate = parseDouble(txtSGST.getText(), 0.0);
        double igstRate = parseDouble(txtIGST.getText(), 0.0);

        cgst = (total * cgstRate) / 100;
        sgst = (total * sgstRate) / 100;
        igst = (total * igstRate) / 100;
        finalAmount = total + cgst + sgst + igst;

        lblTotal.setText(String.format("%.2f", total));
        lblCGST.setText(String.format("%.2f", cgst));
        lblSGST.setText(String.format("%.2f", sgst));
        lblIGST.setText(String.format("%.2f", igst));
        lblTotalAmount.setText(String.format("%.2f", finalAmount));
        lblAmountInWords.setText(InvoiceService.convertToWords(Math.round(finalAmount)));
        amountInWords = InvoiceService.convertToWords(Math.round(finalAmount));
    }

    private double parseDouble(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @FXML
    private void handleEditItem() {
    	InvoiceItem selectedItem = invoiceItemTable.getSelectionModel().getSelectedItem();
    	
    	if(selectedItem == null) {
    		showAlert("Warning", "No item selected.");
    		return;
    	}
    	
    	txtDescription.setText(selectedItem.getDescription());
    	txtHsn.setText(String.valueOf(selectedItem.getHsn()));
    	txtQuantity.setText(String.valueOf(selectedItem.getQuantity()));
    	txtUnitPrice.setText(String.valueOf(selectedItem.getUnitPrice()));
    }
  
    @FXML
    private void handleUpdateItem() {
    	InvoiceItem selectedItem = invoiceItemTable.getSelectionModel().getSelectedItem();
    	
    	if(selectedItem == null) {
    		showAlert("Warning", "No item selelcted.");
    		return;
    	}
    	
    	try {
            int invoiceId = Integer.parseInt(txtInvoiceId.getText().trim());
            String description = txtDescription.getText().trim().toUpperCase();
            int hsn = Integer.parseInt(txtHsn.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            double unitPrice = Double.parseDouble(txtUnitPrice.getText().trim());

            if(invoiceService.isValidInvoiceId(invoiceId)) {
            	showAlert("Invalid input", "Invoice id already exist.");
            	return;
            }
            if(!invoiceService.isValidHsn(hsn)){
            	showAlert("Invalid input", "Enter valid HSN/SAC value");
            	return;
            }
            if(description.isEmpty()) {
            	showAlert("Invalid input", "Please Enter Description");
            	return;
            }
            if(quantity <= 0) {
            	showAlert("Invalid input", "Enter valid quantity.");
            	return;
            }
            if(unitPrice <= 0) {
            	showAlert("Invalid input", "Enter valid unitPrice.");
            	return;
            }
            
            selectedItem.setInvoiceId(invoiceId);
            selectedItem.setDescription(description);
            selectedItem.setHsn(hsn);
            selectedItem.setQuantity(quantity);
            selectedItem.setUnitPrice(unitPrice);
            selectedItem.setAmount(quantity, unitPrice);

            invoiceItemTable.refresh();
            updateInvoiceSummary();
            clearItemFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numerical values.");
        }
    }
  
    @FXML
    private void handleDeleteItem() {
    	InvoiceItem selectedItem = invoiceItemTable.getSelectionModel().getSelectedItem();
    	if(selectedItem == null) {
    		showAlert("Warning", "No item selected.");
    		return;
    	}
    	invoiceItems.remove(selectedItem.getItemId() - 1);
    	int sno = 0;
    	for(InvoiceItem e: invoiceItems) {
    		e.setItemId(++sno);
    	}
    	this.sno = sno;
    	
    	invoiceItemTable.refresh();
        updateInvoiceSummary();
        clearItemFields();
    }
    
    @FXML
    private void handleSaveInvoice() {
    	
    	// Validate required fields
        if (txtInvoiceId.getText().isEmpty() ||
            comboCustomerName.getValue() == null ||
            txtCustomerAddress.getText().isEmpty() ||
            txtGstin.getText().isEmpty() || 
            dpOrderDate.getValue() == null ||
            dpDeliveryDate.getValue() == null ||
            txtSGST.getText().isEmpty() ||
            txtCGST.getText().isEmpty() ||
            invoiceItems.isEmpty()) {
            
            showAlert("Error", "Please fill all fields before saving the invoice.");
            return;
        }
   
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            int invoiceId = Integer.parseInt(txtInvoiceId.getText());
            String customerName = comboCustomerName.getValue();
            String customerAddress = txtCustomerAddress.getText();
            String customerPhone = CustomerDAO.getCustomerById(customerId).getPhone();
            String gstin = txtGstin.getText();
            LocalDate orderDate = dpOrderDate.getValue();
            LocalDate deliveryDate1 =dpDeliveryDate.getValue();
            String deliveryDate = (dpDeliveryDate.getValue() != null) ? dpDeliveryDate.getValue().format(formatter) : "N/A";

            if(invoiceService.isValidInvoiceId(invoiceId)) {
            	showAlert("Invalid input", "Invoice id already exist.");
            	return;
            }
            
            Invoice invoice = new Invoice(invoiceId, customerId, orderDate, deliveryDate1, total, sgst, cgst, igst, Math.round(finalAmount), amountInWords);
            InvoiceDAO.addInvoice(invoice);
            InvoiceItemDAO.addItems(invoiceItems);
            //showAlert("Success", "Invoice saved successully.");
            InvoicePdfGeneration.generateInvoicePDF(invoiceId, customerName, customerAddress, customerPhone, gstin, deliveryDate, invoiceItems,
                    parseDouble(txtSGST.getText(), 0.0), parseDouble(txtCGST.getText(), 0.0), parseDouble(txtIGST.getText(), 0.0),
                    file.getAbsolutePath(), amountInWords);

            showAlert("Success", "Invoice saved successfully!");
            
            try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				showAlert("Error", "Cannot open this pdf file");
			}
        }
    }

    @FXML
    private void clearItemFields() {
        //txtSerialNo.clear();
        txtDescription.clear();
        txtHsn.clear();
        txtQuantity.clear();
        txtUnitPrice.clear();
    }

    @FXML
    private void clearAllFields() {
    	clearItemFields();
        txtInvoiceId.clear();
        comboCustomerName.setValue(null);
        comboCustomerName.getEditor().clear(); 
        txtCustomerAddress.clear();
        txtGstin.clear();;
        dpOrderDate.setValue(null);
        dpDeliveryDate.setValue(null);
        invoiceItems.clear();
        invoiceItemTable.refresh();
        txtCGST.setText("6");
        txtSGST.setText("6");
        txtIGST.clear();
        lblTotal.setText("0.00");
        lblSGST.setText("0.00");
        lblCGST.setText("0.00");
        lblIGST.setText("0.00");
        lblTotalAmount.setText("0.00");
        lblAmountInWords.setText("");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
}
