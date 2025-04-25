package com.str.billing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import com.str.billing.db.DatabaseManager;
import com.str.billing.model.InvoiceItem;

public class InvoiceItemDAO {
	
	public static void addItems(List<InvoiceItem> items) {
		String query = "INSERT into InvoiceItem (invoice_id, description, hsn, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?, ?)";
		try(Connection conn = DatabaseManager.connect()){
			for(InvoiceItem item : items) {
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, item.getInvoiceId());
				pstmt.setString(2, item.getDescription());
				pstmt.setInt(3, item.getHsn());
				pstmt.setInt(4, item.getQuantity());
				pstmt.setDouble(5, item.getUnitPrice());
				pstmt.setDouble(6, item.getAmount());
				
				pstmt.executeUpdate();
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<InvoiceItem> getItemsByInvoiceId(int invoiceId){
		String query = "Select * from InvoiceItem where invoice_id = ?";
		List<InvoiceItem> itemList = new ArrayList<>();
		
		try(Connection conn = DatabaseManager.connect();
			PreparedStatement pstmt = conn.prepareStatement(query);){
			
			pstmt.setInt(1, invoiceId);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				InvoiceItem invoiceItem = new InvoiceItem();
				invoiceItem.setItemId(rs.getInt("invoice_item_id"));
				invoiceItem.setInvoiceId(rs.getInt("invoice_id"));
				invoiceItem.setDescription(rs.getString("description"));
				invoiceItem.setHsn(rs.getInt("hsn"));
				invoiceItem.setQuantity(rs.getInt("quantity"));
				invoiceItem.setUnitPrice(rs.getDouble("unit_price"));
				invoiceItem.setAmount(rs.getDouble("total_price"));
				
				itemList.add(invoiceItem);
			}
		}catch(SQLException e) {
			System.out.println("Error in retriving invoice items" + e.getMessage());
		}
		
		return itemList;
	}
}
