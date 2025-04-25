package com.str.billing.services;

public class CustomerService {

	public boolean isValidName(String name) {
		return name.isEmpty();
	}
	
	public boolean isValidAddress(String address) {
		return address.isEmpty();
	}
	
	public boolean isValidGst(String gst) {
		return gst.matches("^[0-9]{2}[A-Za-z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ][0-9A-Za-z]{1}$");
	}
	
	public boolean isValidPhone(String phone) {
		return phone.matches("^\\d{10}$");
	}
	
}
