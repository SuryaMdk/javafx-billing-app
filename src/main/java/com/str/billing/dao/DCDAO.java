package com.str.billing.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import com.str.billing.db.DatabaseManager;
import com.str.billing.model.DC;

public class DCDAO {

	public static int addDC(DC dc) {
		String query = "INSERT INTO DeliveryChallan(dc_id, customer_id, order_date, delivery_date, created_at) VALUES (?, ?, ?, ?, ?)";
		 
		try(Connection conn = DatabaseManager.connect();
			PreparedStatement pstmt = conn.prepareStatement(query);) {
			
			pstmt.setInt(1, dc.getDcId());
			pstmt.setInt(2, dc.getCustomerId());
			pstmt.setString(3, dc.getOrderDate().toString());
			pstmt.setString(4, dc.getDeliveryDate().toString());
			pstmt.setString(5, dc.getCreatedAt());
			pstmt.executeUpdate();
			System.out.println("DC added Successfully.");
		}catch(SQLException e) {
			System.out.println("Error adding DC: " + e.getMessage());
		}
		return dc.getDcId();
	}
	
	public static boolean dcIdExist(int id) {
		String query = "SELECT COUNT(*) FROM DeliveryChallan where dc_id = ?";
		
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
	
	public static List<DC> getAllDCs(){
		String query = "SELECT * FROM DeliveryChallan";
		List<DC> dcs = new ArrayList<>();
		
		try(Connection conn = DatabaseManager.connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query)){
			
			while(rs.next()) {
				DC dc = new DC();
				dc.setDcId(rs.getInt("dc_id"));
				dc.setCustomerId(rs.getInt("customer_id"));
				dc.setOrderDate(LocalDate.parse(rs.getString("order_date")));
				dc.setDeliveryDate(LocalDate.parse(rs.getString("delivery_date")));
				dc.setCreatedAt(rs.getString("created_at"));
				
				String name = CustomerDAO.getCustomerNameById(rs.getInt("customer_id"));
				dc.setCustomerName(name);
				dcs.add(dc);
			}
				
		}catch(SQLException e) {
			System.out.println("Error in gettin all dcs: " + e.getMessage());
		}
		return dcs;	
	}
	
	public static boolean deleteDCById(int id) {
		String dcQuery = "DELETE FROM DeliveryChallan WHERE dc_id=?";
		String dcItemQuery = "DELETE FROM DCItem WHERE dc_id=?";
		
		try(Connection conn = DatabaseManager.connect();){
			conn.setAutoCommit(false);
			
			try(PreparedStatement pstmt1 = conn.prepareStatement(dcItemQuery)){
				pstmt1.setInt(1, id);
				pstmt1.executeUpdate();
			}
			try(PreparedStatement pstmt2 = conn.prepareStatement(dcQuery)){
				pstmt2.setInt(1, id);
				pstmt2.executeUpdate();
			}
			
			conn.commit();
			return true;
		}catch(SQLException e) {
			System.out.println("Error Deleting DC: " + e.getMessage());
		}
		
		return false;
	}
	
	public static int getAllDCsCount() {
		int count = 0;
		String query = "SELECT COUNT(*) FROM DeliveryChallan";
		
		try(Connection conn = DatabaseManager.connect();){
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			
			if(rs.next())
				count = rs.getInt(1);
		}catch(SQLException e) {
			System.out.println("Error in getting DCs count: " + e.getMessage());
		}
		return count;
	}
}
