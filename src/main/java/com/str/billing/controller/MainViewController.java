package com.str.billing.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MainViewController {

	@FXML
	private AnchorPane contentArea;

	@FXML
	private Button btnDashboard, btnDC, btnDCReports, btnCustomer, btnInvoice, btnInvoiceReports, btnCompany;


	@FXML
	public void initialize() {
		loadView("DashboardView.fxml");	//load dashboard by default.
		btnDashboard.setOnAction(e -> loadView("DashboardView.fxml"));
		btnDC.setOnAction(e -> loadView("DCGeneration.fxml"));
		btnDCReports.setOnAction(e -> loadView("DCReports.fxml"));
		btnInvoice.setOnAction(e -> loadView("InvoiceGeneration.fxml"));
		btnCustomer.setOnAction(e -> loadView("CustomerManagement.fxml"));
		btnInvoiceReports.setOnAction(e -> loadView("InvoiceReports.fxml"));
		btnCompany.setOnAction(e -> loadView("CompanyDetails.fxml"));
	}

	//Utility method to load fxml into the center pane
	public void loadView(String fxmlFile) {
		try {
			FXMLLoader  loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
			Parent view = loader.load();
			
			//check if dashboard is being loaded, and inject controller
			if(fxmlFile.equals("DashboardView.fxml")) {
				DashboardController controller = loader.getController();
				controller.setMainController(this); //Inject this MainViewController
			}
			
			contentArea.getChildren().clear();
			contentArea.getChildren().add(view);
			AnchorPane.setTopAnchor(view, 0.0);
			AnchorPane.setBottomAnchor(view, 0.0);
			AnchorPane.setLeftAnchor(view, 0.0);
			AnchorPane.setRightAnchor(view, 0.0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
