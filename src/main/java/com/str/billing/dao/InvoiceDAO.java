package com.str.billing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.str.billing.db.DatabaseManager;
import com.str.billing.model.Invoice;

public class InvoiceDAO {

	public static int addInvoice(Invoice invoice) {
		String query = "INSERT INTO invoice (invoice_id, customer_id, order_date, delivery_date, total_amount, sgst, cgst, igst, grand_total, amount_in_words, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try(
			Connection conn = DatabaseManager.connect();
			PreparedStatement pstmt = conn.prepareStatement(query);	){
			
			pstmt.setInt(1, invoice.getInvoiceId());
			pstmt.setInt(2, invoice.getCustomerId());
			pstmt.setString(3, invoice.getOrderDate().toString());
			pstmt.setString(4, invoice.getDeliveryDate().toString());
			pstmt.setDouble(5, invoice.getTotalAmount());
			pstmt.setDouble(6, invoice.getSgst());
			pstmt.setDouble(7, invoice.getCgst());
			pstmt.setDouble(8, invoice.getIgst());
			pstmt.setDouble(9, invoice.getGrandTotal());
			pstmt.setString(10, invoice.getAmountInWords());
			pstmt.setString(11, invoice.getCreatedAt());
			pstmt.setString(12, invoice.getUpdatedAt());
			
			pstmt.executeUpdate();
			System.out.println("Invoice added Successfully...");
			
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Error in adding invoice;");
		}
		return invoice.getInvoiceId();
	}

	//Fetch all invoices
	public List<Invoice> getAllInvoices() {

		String sql = "SELECT * FROM Invoice";
		List<Invoice> invoices = new ArrayList<>();

		try(Connection conn = DatabaseManager.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql)){

			while(rs.next()) {
				Invoice invoice = new Invoice();
				invoice.setInvoiceId(rs.getInt("invoice_id"));
				invoice.setCustomerId(rs.getInt("customer_id"));
				invoice.setOrderDate(LocalDate.parse(rs.getString("order_date")));
				invoice.setDeliveryDate(LocalDate.parse(rs.getString("delivery_date")));
				invoice.setCgst(rs.getDouble("cgst"));
				invoice.setSgst(rs.getDouble("sgst"));
				invoice.setIgst(rs.getDouble("igst"));
				invoice.setTotalAmount(rs.getDouble("total_amount"));
				invoice.setGrandTotal(rs.getDouble("grand_total"));
				invoice.setAmountInWords(rs.getString("amount_in_words"));
				invoice.setCreatedAt(rs.getString("created_at"));
				invoice.setUpdatedAt(rs.getString("updated_at"));
				
				String name = CustomerDAO.getCustomerNameById(rs.getInt("customer_id"));
				invoice.setCustomerName(name);
				
				invoices.add(invoice);
			}
		}catch(SQLException e) {
			System.out.println("Error fetching invoices: " + e.getMessage());
		}

		return invoices;
	}

	//Delete an invoice
	public boolean deleteInvoiceById(int invoiceId) {
		String deleteItemSQL = "DELETE FROM InvoiceItem WHERE invoice_id = ?";
		String deleteInvoiceSQL = "DELETE FROM Invoice WHERE invoice_id = ?";

		try(Connection conn = DatabaseManager.connect()){
			//begin transaction
			conn.setAutoCommit(false);

			//Delete item
			try(PreparedStatement pstmt = conn.prepareStatement(deleteItemSQL)){
				pstmt.setInt(1, invoiceId);
				pstmt.executeUpdate();
			}

			//Delete invoice
			try(PreparedStatement pstmt = conn.prepareStatement(deleteInvoiceSQL)){
				pstmt.setInt(1, invoiceId);
				pstmt.executeUpdate();
			}

			//commit transaction
			conn.commit();
			return true;
		}catch(SQLException e) {
			System.out.println("Error deleting invoice: " + e.getMessage());
		}
		return false;
	}

	//Check invoice id exist in the db table
	public static boolean invoiceIdExist(int id) {
		String query = "SELECT COUNT(*) FROM invoice where invoice_id = ?";
		
		try(Connection conn = DatabaseManager.connect();
			PreparedStatement pstmt = conn.prepareStatement(query);){
				
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt(1)>0;
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}	
		return false;
	}
	
	public static int getAllInvoicesCount() {
		int count = 0;
		String query = "SELECT COUNT(*) FROM INVOICE";
		
		try(Connection conn = DatabaseManager.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){
			
			if(rs.next())
				count = rs.getInt(1);
		}catch(SQLException e) {
			System.out.print("Error in getting invoices count: " + e.getMessage());
		}
		return count;
	}

}
