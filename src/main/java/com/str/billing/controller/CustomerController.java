package com.str.billing.controller;

import java.util.Optional;

import com.str.billing.dao.CustomerDAO;
import com.str.billing.model.Customer;
import com.str.billing.services.CustomerService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


public class CustomerController {

	@FXML private TextField txtCustomerName, txtAddress, txtPhone, txtGst;

	@FXML private TableView<Customer> customerTable;
	@FXML private TableColumn<Customer, Void> colSno;
	@FXML private TableColumn<Customer, Integer> colCustomerId;
	@FXML private TableColumn<Customer, String>  colName, colAddress, colPhone, colGst;

	private final ObservableList<Customer> customerList = FXCollections.observableArrayList();
	private final CustomerDAO customerDAO = new CustomerDAO();
	private final CustomerService customerService= new CustomerService();

	public void initialize() {
		//set up table columns
		colCustomerId.setCellValueFactory(data -> data.getValue().customerIdProperty().asObject());
		colName.setCellValueFactory(data -> data.getValue().nameProperty());
		colAddress.setCellValueFactory(data -> data.getValue().addressProperty());
		colGst.setCellValueFactory(data -> data.getValue().gstNumberPrperty());
		colPhone.setCellValueFactory(data -> data.getValue().phoneProperty());

		loadCustomers();
		customerTable.setItems(customerList);
		
		//set serial number to the valid rows in customer table
		colSno.setCellFactory(col -> new TableCell<Customer, Void>() {
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
	}

	private void loadCustomers() {
		customerList.clear();
		customerList.addAll(customerDAO.getAllCustomers());
	}

	@FXML
	private void handleAddCustomer() {
		try {
			String name = txtCustomerName.getText().toUpperCase();
			String address = txtAddress.getText().toUpperCase();
			String phone = txtPhone.getText();
			String gstNumber = txtGst.getText().toUpperCase();

			if(customerService.isValidName(name)) {
				showAlert("Warning", "Fill Customer Name.");
				return;
			}
			if(customerService.isValidAddress(address)) {
				showAlert("Warning", "Fill Customer Address");
				return;
			}
			if(!customerService.isValidGst(gstNumber)) {
				showAlert("Warning", "Fill Customer valid Gstin");
				return;
			}
			if(!customerService.isValidPhone(phone)) {
				showAlert("Warning", "Fill Customer Valid Phone number");
				return;
			}
			
			Customer customer = new Customer(name, address, gstNumber, phone);
			customerDAO.addCustomer(customer);
			showAlert("Success", "Customer Added Successfully.");
			loadCustomers();
			clearFields();
		}catch(NumberFormatException e) {
			showAlert("Error", "Customer ID must be in valid number.");
		}
		System.out.println("Customer Added: " + txtCustomerName.getText());
	}

	@FXML
	private void handleEditCustomer() {
		Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
		if(selectedCustomer == null) {
			showAlert("Warning!", "No Customer Selected.");
			return;
		}
		txtCustomerName.setText(selectedCustomer.getName());
		txtAddress.setText(selectedCustomer.getAddress());
		txtPhone.setText(selectedCustomer.getPhone());
		txtGst.setText(selectedCustomer.getGstNumber());
	}
	
	@FXML
	private void handleUpdateCustomer() {
		Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
		if(selectedCustomer == null) {
			showAlert("Warning!", "No Customer selected.");
			return;
		}

		String name = txtCustomerName.getText();
		String address = txtAddress.getText();
		String phone = txtPhone.getText();
		String gstNumber = txtGst.getText();
		
		if(customerService.isValidName(name)) {
			showAlert("Warning", "Fill Customer Name.");
			return;
		}
		if(customerService.isValidAddress(address)) {
			showAlert("Warning", "Fill Customer Address");
			return;
		}
		if(!customerService.isValidGst(gstNumber)) {
			showAlert("Warning", "Fill Customer valid Gstin");
			return;
		}
		if(!customerService.isValidPhone(phone)) {
			showAlert("Warning", "Fill Customer Valid Phone number");
			return;
		}
		
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirm Update");
		confirmationAlert.setHeaderText("Are you sure you want to Update customer?");
		confirmationAlert.setContentText("Customer: " + selectedCustomer.getName());
		
		Optional<ButtonType> result = confirmationAlert.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.OK) {
			selectedCustomer.setName(txtCustomerName.getText().toUpperCase());
			selectedCustomer.setAddress(txtAddress.getText().toUpperCase());
			selectedCustomer.setGstNumber(txtGst.getText().toUpperCase());
			selectedCustomer.setPhone(txtPhone.getText());

			customerDAO.updateCustomer(selectedCustomer);
			loadCustomers();
			clearFields();
			showAlert("Success","Customer Edited Successfully.");
			System.out.println("Customer details Edited successful..");
		}else {
			System.out.println("Customer Updation cancelled.");
		}
		
	}

	@FXML
	private void handleClearFields() {
		clearFields();
	}

	private void clearFields() {
		txtCustomerName.clear();
		txtAddress.clear();
		txtPhone.clear();
		txtGst.clear();
	}

	@FXML
	private void handleDeleteCustomer() {
		Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
		if(selectedCustomer == null) {
			showAlert("Warning!", "No Customer selected.");
			return;
		}
		
		Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationAlert.setTitle("Confirm Deletion");
		confirmationAlert.setHeaderText("Are you sure you want to delete customer?");
		confirmationAlert.setContentText("Customer: " + selectedCustomer.getName());
		
		Optional<ButtonType> result = confirmationAlert.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.OK) {
			customerDAO.deleteCustomer(selectedCustomer.getCustomerId());
			loadCustomers();
			showAlert("Success", "Customer Deleted Successfully.");
			System.out.println("Customer deleted.");
		}else {
			System.out.println("Customer Deletion cancelled.");
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
