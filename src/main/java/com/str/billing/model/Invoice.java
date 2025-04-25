package com.str.billing.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import javafx.beans.property.*;


public class Invoice {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
    private final IntegerProperty invoiceId = new SimpleIntegerProperty();
    private final IntegerProperty customerId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> orderDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> deliveryDate = new SimpleObjectProperty<>();
    private final DoubleProperty totalAmount = new SimpleDoubleProperty();
    private final DoubleProperty cgst = new SimpleDoubleProperty();
    private final DoubleProperty sgst = new SimpleDoubleProperty();
    private final DoubleProperty igst = new SimpleDoubleProperty();
    private final DoubleProperty grandTotal = new SimpleDoubleProperty();
    private final StringProperty amountInWords = new SimpleStringProperty();
    private final StringProperty createdAt = new SimpleStringProperty();
    private final StringProperty updatedAt = new SimpleStringProperty();
    
    private final StringProperty customerName = new SimpleStringProperty();	//for ui display


    public Invoice() {}
    
    public Invoice(int invoiceId, int customerId, LocalDate orderDate, LocalDate deliveryDate, double totalAmount,
			double cgst, double sgst, double igst, double grandTotal, String amountInWords) {
		super();
		this.invoiceId.set(invoiceId);
		this.customerId.set(customerId);
		this.orderDate.set(orderDate);
		this.deliveryDate.set(deliveryDate);
		this.totalAmount.set(totalAmount);
		this.cgst.set(cgst);
		this.sgst.set(sgst);
		this.igst.set(igst);
		this.grandTotal.set(grandTotal);
		this.amountInWords.set(amountInWords);
		this.createdAt.set(LocalDateTime.now().format(formatter));
		this.updatedAt.set(LocalDateTime.now().format(formatter));
	}

    public Invoice(int invoiceId, int customerId, LocalDate orderDate, LocalDate deliveryDate, double totalAmount,
			double cgst, double sgst, double igst, double grandTotal, String amountInWords, String customerName) {
		super();
		this.invoiceId.set(invoiceId);
		this.customerId.set(customerId);
		this.orderDate.set(orderDate);
		this.deliveryDate.set(deliveryDate);
		this.totalAmount.set(totalAmount);
		this.cgst.set(cgst);
		this.sgst.set(sgst);
		this.igst.set(igst);
		this.grandTotal.set(grandTotal);
		this.amountInWords.set(amountInWords);
		this.createdAt.set(LocalDateTime.now().format(formatter));
		this.updatedAt.set(LocalDateTime.now().format(formatter));
		this.customerName.set(customerName);
	}
    
    public int getInvoiceId() { return invoiceId.get(); }
    public void setInvoiceId(int invoiceId) { this.invoiceId.set(invoiceId); }
    public IntegerProperty invoiceIdProperty() { return invoiceId;    }
    
    public int getCustomerId() { return customerId.get(); }
    public void setCustomerId(int customerId) { this.customerId.set(customerId); }
    public IntegerProperty customerIdProperty() { return customerId; }

    public LocalDate getOrderDate() { return orderDate.get(); }
    public void setOrderDate(LocalDate orderDate) { this.orderDate.set(orderDate); }
    public ObjectProperty<LocalDate> orderDateProperty() { return orderDate; }

    public LocalDate getDeliveryDate() { return deliveryDate.get(); }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate.set(deliveryDate); }
    public ObjectProperty<LocalDate> deliveryDateProperty() { return deliveryDate; }

    public double getTotalAmount() { return totalAmount.get(); }
    public void setTotalAmount(double totalAmount) { this.totalAmount.set(totalAmount); }
    public DoubleProperty totalAmountProperty() { return totalAmount; }

    public double getCgst() { return cgst.get(); }
    public void setCgst(double cgst) { this.cgst.set(cgst); }
    public DoubleProperty cgstProperty() { return cgst; }

    public double getSgst() { return sgst.get(); }
    public void setSgst(double sgst) { this.sgst.set(sgst); }
    public DoubleProperty sgstProperty() { return sgst; }

    public double getIgst() { return igst.get(); }
    public void setIgst(double igst) { this.igst.set(igst); }
    public DoubleProperty igstProperty() { return igst; }

    public double getGrandTotal() { return grandTotal.get(); }
    public void setGrandTotal(double grandTotal) { this.grandTotal.set(grandTotal); }
    public DoubleProperty grandTotalProperty() { return grandTotal; }

    public String getAmountInWords() { return amountInWords.get(); }
    public void setAmountInWords(String amountInWords) { this.amountInWords.set(amountInWords); }
    public StringProperty amountInWordsProperty() { return amountInWords; }

    public String getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(String createdAt) { this.createdAt.set(createdAt); }
    public StringProperty createdAtProperty() { return createdAt; }

    public String getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(String updatedAt) { this.updatedAt.set(updatedAt); }
    public StringProperty updatedAtProperty() { return updatedAt; }
    
    public String getCustomerName() { return customerName.get();    }
    public void setCustomerName(String customerName) { this.customerName.set(customerName); }
    public StringProperty custoemerNameProperty() { return customerName; }
}