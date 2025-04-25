package com.str.billing.model;

import javafx.beans.property.*;

public class InvoiceItem {
    private final IntegerProperty itemId = new SimpleIntegerProperty();
    private final IntegerProperty invoiceId = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty hsn = new SimpleIntegerProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final DoubleProperty unitPrice = new SimpleDoubleProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();

    public InvoiceItem() {}
    
    public InvoiceItem(int itemId, int invoiceId, String description, int hsn, int quantity, double unitPrice) {
        this.itemId.set(itemId);
    	this.invoiceId.set(invoiceId);
        this.description.set(description);
        this.hsn.set(hsn);
        this.quantity.set(quantity);
        this.unitPrice.set(unitPrice);
        this.amount.set(quantity * unitPrice);
    }

    public InvoiceItem(int invoiceId, String description, int hsn, int quantity, double unitPrice) {
    	this.invoiceId.set(invoiceId);
        this.description.set(description);
        this.hsn.set(hsn);
        this.quantity.set(quantity);
        this.unitPrice.set(unitPrice);
        this.amount.set(quantity * unitPrice);
    }

    public int getItemId() { return itemId.get(); }
    public void setItemId(int itemId) { this.itemId.set(itemId); }
    public IntegerProperty itemIdProperty() { return itemId; }

    public int getInvoiceId() { return invoiceId.get(); }
    public void setInvoiceId(int invoiceId) { this.invoiceId.set(invoiceId); }
    public IntegerProperty invoiceIdProperty() { return invoiceId; }
    
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public int getHsn() { return hsn.get(); }
    public void setHsn(int hsn) { this.hsn.set(hsn); }
    public IntegerProperty hsnProperty() { return hsn; }
    
    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    public double getUnitPrice() { return unitPrice.get(); }
    public void setUnitPrice(double unitPrice) { this.unitPrice.set(unitPrice); }
    public DoubleProperty unitPriceProperty() { return unitPrice; }

    public double getAmount() { return amount.get(); }
    public void setAmount(double amount) { this.amount.set(amount); }
    public void setAmount(double quantity, double unitPrice) { this.amount.set(quantity * unitPrice); }
    public ReadOnlyDoubleProperty amountProperty() { return amount; }
}
