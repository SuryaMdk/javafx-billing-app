package com.str.billing.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {

	private final IntegerProperty customerId = new SimpleIntegerProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty address = new SimpleStringProperty();
	private final StringProperty gstNumber = new SimpleStringProperty();
	private final StringProperty phone = new SimpleStringProperty();
	
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Customer(int id, String name, String address, String gst, String phone) {
		this.customerId.set(id);
		this.name.set(name);
		this.address.set(address);
		this.gstNumber.set(gst);
		this.phone.set(phone);
	}
	
    public Customer( String name, String address, String gst, String phone) {
    	this.name.set(name);
		this.address.set(address);
		this.gstNumber.set(gst);
		this.phone.set(phone);
	}

	public IntegerProperty customerIdProperty() { return customerId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty addressProperty() { return address; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty gstNumberPrperty() { return gstNumber; }

    public int getCustomerId() { return customerId.get(); }
    public String getName() { return name.get(); }
    public String getAddress() { return address.get(); }
    public String getPhone() { return phone.get(); }
    public String getGstNumber() { return gstNumber.get(); }

    public void setCustomerId(int id) { this.customerId.set(id);}
    public void setName(String name) { this.name.set(name); }
    public void setAddress(String address) { this.address.set(address); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public void setGstNumber(String gstNumber) { this.gstNumber.set(gstNumber);}
}
