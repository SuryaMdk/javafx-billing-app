package com.str.billing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.str.billing.db.DatabaseManager;
import com.str.billing.model.Customer;

public class CustomerDAO {

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customer";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("gst_number"),
                    rs.getString("phone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public void addCustomer(Customer customer) {
        String query = "INSERT INTO customer (name, address, gst_number, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            //pstmt.setInt(1, customer.getCustomerId());
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getAddress());
            pstmt.setString(3, customer.getGstNumber());
            pstmt.setString(4, customer.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer(Customer customer) {
        String query = "UPDATE customer SET name=?, address=?, gst_number=?, phone=? WHERE id=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getAddress());
            pstmt.setString(3, customer.getGstNumber());
            pstmt.setString(4, customer.getPhone());
            pstmt.setInt(5, customer.getCustomerId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCustomer(int customerId) {
        String query = "DELETE FROM customer WHERE id=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Customer getCustomerById(int customerId) {
        String query = "SELECT * FROM customer WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("gst_number"),
                    rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving customer: " + e.getMessage());
        }
        return null; // Return null if customer not found
    }
    
    public static Map<String, Customer> getAllCustomersMap() {
        Map<String, Customer> customerMap = new HashMap<>();
        String sql = "SELECT * FROM customer";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("id"));
                customer.setName(rs.getString("name"));
                customer.setAddress(rs.getString("address"));
                customer.setPhone(rs.getString("phone"));
                customer.setGstNumber(rs.getString("gst_number"));
                customerMap.put(customer.getName(), customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerMap;
    }
    
    public static String getCustomerNameById(int customerId) {
    	String query = "SELECT name FROM customer where id = ?";
    	
    	try(Connection conn = DatabaseManager.connect();
    		PreparedStatement pstmt = conn.prepareStatement(query);){
    		
    		pstmt.setInt(1, customerId);
    		ResultSet rs = pstmt.executeQuery();
    		
    		if(rs.next()) {
    			return rs.getString("name");
    		}
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return "UNKNOWN CUSTOMER";
    }
    
    public static int getAllCustomersCount() {
    	int count = 0;
    	String query = "SELECT COUNT(*) FROM Customer";
    	try(Connection conn = DatabaseManager.connect();
    		Statement stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery(query);){
    		
    		if(rs.next())
    			count = rs.getInt(1);
    	}catch(SQLException e) {
    		System.out.println("Error in getting customer count: " + e.getMessage());
    	}
    	return count;
    }

}
