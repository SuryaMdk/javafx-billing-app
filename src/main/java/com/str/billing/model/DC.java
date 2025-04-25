package com.str.billing.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.*;

public class DC {

	private final IntegerProperty dcId = new SimpleIntegerProperty();
	private final IntegerProperty customerId = new SimpleIntegerProperty();
	private final StringProperty customerName = new SimpleStringProperty();	//for ui display not to db
	private final ObjectProperty<LocalDate> orderDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> deliveryDate = new SimpleObjectProperty<>();
    private final StringProperty createdAt = new SimpleStringProperty();
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
	public DC() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public DC(int dcId, int customerId, LocalDate orderDate, LocalDate deliverDate) {
		this.dcId.set(dcId);
		this.customerId.set(customerId);
		this.orderDate.set(orderDate);
		this.deliveryDate.set(deliverDate);
		this.createdAt.set(LocalDateTime.now().format(formatter));
	}
	
	public DC(int dcId, int customerId, LocalDate orderDate, LocalDate deliverDate, String CustomerName) {
		this.dcId.set(dcId);
		this.customerId.set(customerId);
		this.orderDate.set(orderDate);
		this.deliveryDate.set(deliverDate);
		this.createdAt.set(LocalDateTime.now().format(formatter));
		this.customerName.set(CustomerName);
	}
	
	public int getDcId() { return dcId.get(); }
	public void setDcId(int dcId) { this.dcId.set(dcId); }
	public IntegerProperty dcIdProperty() { return dcId; }
	
	public int getCustomerId() { return customerId.get(); }
	public void setCustomerId(int customerId) { this.customerId.set(customerId); }
	public IntegerProperty customerIdProperty() { return customerId; }
	
	public LocalDate getOrderDate() { return orderDate.get(); }
	public void setOrderDate(LocalDate orderDate) { this.orderDate.set(orderDate); }
	public ObjectProperty<LocalDate> orderDateProperty(){ return orderDate; }
	
	public LocalDate getDeliveryDate() { return deliveryDate.get(); }
	public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate.set(deliveryDate); }
	public ObjectProperty<LocalDate> deliveryDateProperty(){ return deliveryDate; }
	
	public String getCreatedAt() { return createdAt.get(); }
	public void setCreatedAt(String createdAt) { this.createdAt.set(createdAt); }
	public StringProperty createdAtProperty() { return createdAt; }
	
	public String getCustomerName() { return customerName.get(); }
	public void setCustomerName(String customerName) { this.customerName.set(customerName);	}
	public StringProperty customerNameProperty() { return customerName; }
	
}
