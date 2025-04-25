package com.str.billing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import com.str.billing.db.DatabaseManager;
import com.str.billing.model.DCItem;

public class DCItemDAO {

	public static void addItems(List<DCItem> dcItems) {
		String query = "INSERT INTO DcItem(dc_item_id, dc_id, description, quantity, remarks) VALUES(?, ?, ?, ?, ?)";
		
		try(Connection conn = DatabaseManager.connect();
			PreparedStatement pstmt = conn.prepareStatement(query);){
			for(DCItem item: dcItems) {
				pstmt.setInt(1, item.getItemId());
				pstmt.setInt(2, item.getDcId());
				pstmt.setString(3, item.getDescription());
				//pstmt.setInt(4, item.getHsn());
				pstmt.setInt(4, item.getQuantity());
				pstmt.setString(5, item.getRemarks());
				
				pstmt.executeUpdate();
			}
		}catch(SQLException e) {
			System.out.println("Error adding Dc items:" + e.getMessage());
		}
	}

	public static List<DCItem> getItemsByDCId(int dcId){
		String query = "SELECT * FROM DCItem WHERE dc_id = ?";
		List<DCItem> items = new ArrayList<>();
		
		try(Connection conn = DatabaseManager.connect();
			PreparedStatement pstmt = conn.prepareStatement(query);){
			
			pstmt.setInt(1, dcId);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				DCItem item = new DCItem();
				item.setDcId(rs.getInt("dc_id"));
				item.setDescription(rs.getString("description"));
				item.setItemId(rs.getInt("dc_item_id"));
				item.setQuantity(rs.getInt("quantity"));
				item.setRemarks(rs.getString("remarks"));
				
				items.add(item);
			}
		}catch(SQLException e) {
			System.out.println("Error in getting items by dc id: " + e.getMessage());
		}
		return items;
	}
}
