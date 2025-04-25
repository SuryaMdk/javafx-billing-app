package com.str.billing.model;

import javafx.beans.property.*;

public class DCItem {

	private final IntegerProperty itemId = new SimpleIntegerProperty();
	private final IntegerProperty dcId = new SimpleIntegerProperty();
	private final StringProperty description = new SimpleStringProperty();
	//private final IntegerProperty hsn = new SimpleIntegerProperty();
	private final IntegerProperty quantity = new SimpleIntegerProperty();
	private final StringProperty remarks = new SimpleStringProperty();
	
	public DCItem() {}
	
	public DCItem(int itemId, int dcId, String description, int quantity, String remarks) {
		this.dcId.set(dcId);
		this.itemId.set(itemId);
		this.description.set(description);
		//this.hsn.set(hsn);
		this.quantity.set(quantity);
		this.remarks.set(remarks);
	}
	
	public int getItemId() { return itemId.get(); }
	public void setItemId(int itemId) { this.itemId.set(itemId); }
	
	public int getDcId() { return dcId.get();}
	public void setDcId(int dcId) { this.dcId.set(dcId); }
	
	public String getDescription() { return description.get(); }
	public void setDescription(String description) { this.description.set(description); }
	
//	public int getHsn() { return hsn.get(); }
//	public void setHsn(int hsn) { this.hsn.set(hsn); }
	
	public int getQuantity() { return quantity.get(); }
	public void setQuantity(int quantity) { this.quantity.set(quantity); }
	
	public String getRemarks() { return remarks.get();	}
	public void setRemarks(String remarks) { this.remarks.set(remarks); }
	
	public IntegerProperty itemIdProperty() {
		return itemId;
	}
	public IntegerProperty dcIdProperty() {
		return dcId;
	}
	public StringProperty descriptionProperty() {
		return description;
	}
//	public IntegerProperty hsnProperty() {
//		return hsn;
//	}
	public IntegerProperty quantityProperty() {
		return quantity;
	}
	public StringProperty remarksProperty() {
		return remarks;
	}
	
	
}
