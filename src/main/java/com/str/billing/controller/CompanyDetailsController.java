package com.str.billing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CompanyDetailsController {

    @FXML private TextField companyNameField, addressField, panField, gstinField, 
    			phoneNumberField, bankNameField, accountNumberField, ifscCodeField, emailField, websiteField;


    @FXML
    private void handleSave() {
        String name = companyNameField.getText();
        String address = addressField.getText();
        String gstin = gstinField.getText();
        String phone = phoneNumberField.getText();
        String bank = bankNameField.getText();
        String account = accountNumberField.getText();
        String ifsc = ifscCodeField.getText();
        String email = emailField.getText();

        // TODO: Save this data to DB or file
        System.out.println("Saved company info:");
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("GSTIN: " + gstin);
        System.out.println("Phone: " + phone);
        System.out.println("Bank: " + bank);
        System.out.println("Account No: " + account);
        System.out.println("IFSC: " + ifsc);
        System.out.println("Email: " + email);
    }

    @FXML
    private void handleClear() {
        companyNameField.clear();
        addressField.clear();
        gstinField.clear();
        panField.clear();
        websiteField.clear();
        phoneNumberField.clear();
        bankNameField.clear();
        accountNumberField.clear();
        ifscCodeField.clear();
        emailField.clear();
    }
}
