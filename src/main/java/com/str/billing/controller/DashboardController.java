package com.str.billing.controller;

import java.io.File;

import com.str.billing.dao.*;
import com.str.billing.util.BackupUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label lblTotalCustomers, lblTotalInvoices, lblTotalDCs;

    @FXML private Button btnManageCustomers, btnGenerateInvoice, btnInvoiceReports,
                         btnGenerateDC, btnDCReports, btnManualBackup;

    private MainViewController mainViewController;

    public void setMainController(MainViewController controller) {
        this.mainViewController = controller;
    }

    @FXML
    public void initialize() {
        loadDashboardData();
        btnManageCustomers.setOnAction(e -> navigateTo("CustomerManagement.fxml"));
        btnGenerateInvoice.setOnAction(e -> navigateTo("InvoiceGeneration.fxml"));
        btnInvoiceReports.setOnAction(e -> navigateTo("InvoiceReports.fxml"));
        btnGenerateDC.setOnAction(e -> navigateTo("DCGeneration.fxml"));
        btnDCReports.setOnAction(e -> navigateTo("DCReports.fxml"));
        btnManualBackup.setOnAction(e -> onManualBackupClicked());
    }

    private void loadDashboardData() {
        int totalCustomers = CustomerDAO.getAllCustomersCount();	//get total count from customer table
        int totalInvoices = InvoiceDAO.getAllInvoicesCount();	//get invoice total count from invoice table
        int totalDCs = DCDAO.getAllDCsCount();	//get dc total from dc table

        lblTotalCustomers.setText(String.valueOf(totalCustomers));
        lblTotalInvoices.setText(String.valueOf(totalInvoices));
        lblTotalDCs.setText(String.valueOf(totalDCs));
    }

    private void navigateTo(String fxmlFile) {
        if (mainViewController != null) {
            mainViewController.loadView(fxmlFile);
        } else {
            System.err.println("MainViewController not set in DashboardController.");
        }
    }
    
    private void onManualBackupClicked() {
        String APP_FOLDER = System.getProperty("user.home") + File.separator + "BillingApp";
        String DB_FILE = "billing.db";
        String DB_PATH = APP_FOLDER + File.separator + DB_FILE;
        
        String BackupFolder = System.getProperty("user.home") + File.separator + "BillingAppBackups";
        
        BackupUtil.backupDatabase(DB_PATH, BackupFolder);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("DB backup successfully completed.");
        alert.setContentText("File location: " + BackupFolder);
        alert.showAndWait();
    }
}
