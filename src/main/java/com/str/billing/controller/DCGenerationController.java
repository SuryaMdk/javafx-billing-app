package com.str.billing.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.str.billing.dao.CustomerDAO;
import com.str.billing.dao.DCDAO;
import com.str.billing.dao.DCItemDAO;
import com.str.billing.model.*;
import com.str.billing.services.DCService;
import com.str.billing.util.DCPdfGenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class DCGenerationController {

    @FXML private TextField txtDCId,txtDescription, txtHsn, txtQuantity;
    @FXML private DatePicker dpOrderDate,dpDeliveryDate;
    @FXML private ComboBox<String> comboCustomerName, txtRemarks;
    @FXML private TableView<DCItem> dcItemTable;
    @FXML private TableColumn<DCItem, Integer> colSerialNo, colHsn, colQuantity;
    @FXML private TableColumn<DCItem, String> colDescription, colUnitPrice, colAmount;

    private Map<String, Customer> customerMap;
    private ObservableList<DCItem> dcItems = FXCollections.observableArrayList();
    private int itemId = 0, customerId;
    
    @FXML
    public void initialize() {
    	//Load customer names from DB
    	customerMap = CustomerDAO.getAllCustomersMap();
    	ObservableList<String> names = FXCollections.observableArrayList(customerMap.keySet());
    	comboCustomerName.setItems(names.sorted());
    	
    	//Auto fill customer id
        comboCustomerName.valueProperty().addListener((obs, oldVal, newVal)-> {
        	Customer selected = customerMap.get(newVal);
        	if(selected != null) {
        		customerId = selected.getCustomerId();
        	}
        });
        
        // Fill combo items for remarks
        txtRemarks.getItems().addAll(
        	    "For VMC job work only",
        	    "For VMC and EDM job work only",
        	    "For VMC rework only",
        	    "For VMC reconditioning work only"
        	);

        colSerialNo.setCellValueFactory(cellData -> cellData.getValue().itemIdProperty().asObject());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
       // colHsn.setCellValueFactory(cellData -> cellData.getValue().hsnProperty().asObject());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        colAmount.setCellValueFactory(cellData -> cellData.getValue().remarksProperty());
        dcItemTable.setItems(dcItems);
    }

    @FXML
    private void handleAddItem() {
    	 try {
             int dcId =  Integer.parseInt(txtDCId.getText().trim());
             String description = txtDescription.getText().trim().toUpperCase();
            // int hsn = Integer.parseInt(txtHsn.getText().trim());
             int quantity = Integer.parseInt(txtQuantity.getText().trim());
             String remarks =txtRemarks.getEditor().getText().trim().toUpperCase();

             if(DCService.isValidDCId(dcId)) {
             	showAlert("Invalid input", "DC id already exist.");
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
             if(remarks.isEmpty()) {
             	showAlert("Invalid input", "Enter valid unitPrice.");
             	return;
             }
             
             DCItem item = new DCItem(++itemId, dcId, description, quantity, remarks);
             
             dcItems.add(item);
             dcItemTable.refresh();
             clearItemFields();
         } catch (NumberFormatException e) {
             showAlert("Invalid Input", "Please enter valid numerical values before adding item.");
         }
    }
   
    @FXML
    private void handleEditItem() {
    	DCItem selectedItem = dcItemTable.getSelectionModel().getSelectedItem();
    	
    	if(selectedItem == null) {
    		showAlert("Warning", "No item selected.");
    		return;
    	}
    	
    	txtDescription.setText(selectedItem.getDescription());
    	//txtHsn.setText(String.valueOf(selectedItem.getHsn()));
    	txtQuantity.setText(String.valueOf(selectedItem.getQuantity()));
    	txtRemarks.setValue(selectedItem.getRemarks());
    }
  
    @FXML
    private void handleUpdateItem() {
    	DCItem selectedItem = dcItemTable.getSelectionModel().getSelectedItem();
    	if(selectedItem == null) {
    		showAlert("Warning", "No item selected.");
    		return;
    	}
    	
      	 try {
             int dcId =  Integer.parseInt(txtDCId.getText().trim());
             String description = txtDescription.getText().trim().toUpperCase();
            // int hsn = Integer.parseInt(txtHsn.getText().trim());
             int quantity = Integer.parseInt(txtQuantity.getText().trim());
             String remarks =txtRemarks.getEditor().getText().trim().toUpperCase();
     
             if(DCService.isValidDCId(dcId)) {
             	showAlert("Invalid input", "DC id already exist.");
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
             if(remarks.isEmpty()) {
             	showAlert("Invalid input", "Enter valid unitPrice.");
             	return;
             }
             
             selectedItem.setDcId(dcId);
             selectedItem.setDescription(description);
             //selectedItem.setHsn(hsn);
             selectedItem.setQuantity(quantity);
             selectedItem.setRemarks(remarks);

             dcItemTable.refresh();
             clearItemFields();
         } catch (NumberFormatException e) {
             showAlert("Invalid Input", "Please enter valid numerical values before adding item.");
         }
    }
  
    @FXML
    private void handleDeleteItem() {
    	DCItem selectedItem = dcItemTable.getSelectionModel().getSelectedItem();
    	if(selectedItem == null) {
    		showAlert("Warning", "No item selected.");
    		return;
    	}
    	dcItems.remove(selectedItem.getItemId() - 1);
    	int sno = 0;
    	for(DCItem e: dcItems) {
    		e.setItemId(++sno);
    	}
    	dcItemTable.refresh();
        clearItemFields();
    }
    
    @FXML
    private void handleSaveDC() {
    	// Validate required fields
        if (txtDCId.getText().isEmpty() ||
            comboCustomerName.getValue() == null ||
            dpOrderDate.getValue() == null ||
            dpDeliveryDate.getValue() == null ||
            dcItems.isEmpty()) {
            
            showAlert("Error", "Please fill all fields before saving the Delivery Challan.");
            return;
        }
   
        // choose file to save dc
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save DC file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            int dcId = Integer.parseInt(txtDCId.getText());
            LocalDate orderDate = dpOrderDate.getValue();
            LocalDate deliveryDate =dpDeliveryDate.getValue();

            if(DCService.isValidDCId(dcId)) {
            	showAlert("Invalid input", "DC id already exist.");
            	return;
            }
            
            DC dc = new DC(dcId, customerId, orderDate, deliveryDate);
            DCDAO.addDC(dc);
            DCItemDAO.addItems(dcItems);
            Customer customer = CustomerDAO.getCustomerById(customerId);
            
			//Generate the DC pdf
			DCPdfGenerator.generateDCPdf(dc, customer, dcItems, file.getAbsolutePath());

            showAlert("Success", "Delivery Challan saved successfully!");
            
            try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				showAlert("Error", "Cannot open this pdf file");
			}
        }
    }

    @FXML
    private void clearItemFields() {
        txtDescription.clear();
        //txtHsn.clear();
        txtQuantity.clear();
        txtRemarks.setValue(null);
        txtRemarks.getEditor().clear();
    }

    @FXML
    private void clearAllFields() {
    	clearItemFields();
        txtDCId.clear();
        comboCustomerName.setValue(null);
        comboCustomerName.getEditor().clear(); 
        dpOrderDate.setValue(null);
        dpDeliveryDate.setValue(null);
        dcItems.clear();
        dcItemTable.refresh();
        itemId = 0;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
}
