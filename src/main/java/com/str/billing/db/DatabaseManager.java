package com.str.billing.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

	
//	private static final String URL = "jdbc:sqlite:billing.db";
//
//	public static Connection connect(){
//
//		try {
//			Connection conn = DriverManager.getConnection(URL);
//            initializeDatabase(conn);
//            return conn;
//		} catch (SQLException e) {
//			System.out.println("Connection Failed: " + e.getMessage());
//			return null;
//		}
//	}
	
    // Define the folder path where we want to store the writable DB file (e.g., C:\Users\surya\BillingApp)
    private static final String APP_FOLDER = System.getProperty("user.home") + File.separator + "BillingApp";

    // The database file name
    private static final String DB_FILE = "billing.db";

    // Full path to the DB file
    private static final String DB_PATH = APP_FOLDER + File.separator + DB_FILE;

    // JDBC URL used to connect to SQLite
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection connect() {
        try {
            // 1. Create the directory if it doesn't exist
            Files.createDirectories(Paths.get(APP_FOLDER));

            // 2. If the DB doesn't exist yet, copy the initial version from the application resources
            Path destination = Paths.get(DB_PATH);
            if (!Files.exists(destination)) {
                // Load billing.db from inside the JAR (src/main/resources)
                InputStream in = DatabaseManager.class.getResourceAsStream("/billing.db");
                if (in != null) {
                    Files.copy(in, destination);
                }
            }

            // 3. Connect to the database
            Connection conn = DriverManager.getConnection(URL);
            initializeDatabase(conn);
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch( SQLException e) {
        	 e.printStackTrace();
             return null;
        }
    }

	private static void initializeDatabase(Connection conn) {
		String createCustomerTable = "CREATE TABLE IF NOT EXISTS Customer ("
				+ "    id         INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "    name       TEXT    NOT NULL,"
				+ "    address    TEXT    NOT NULL,"
				+ "    gst_number TEXT,"
				+ "    phone      TEXT"
				+ ");";

        String createInvoiceTable = "CREATE TABLE IF NOT EXISTS Invoice ("
        		+ "    id              INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ "    invoice_id      INTEGER NOT NULL,"
        		+ "    customer_id     INTEGER NOT NULL,"
        		+ "    order_date      TEXT    NOT NULL,"
        		+ "    delivery_date   TEXT    NOT NULL,"
        		+ "    total_amount    REAL,"
        		+ "    sgst            REAL,"
        		+ "    cgst            REAL,"
        		+ "    igst            REAL,"
        		+ "    grand_total     REAL,"
        		+ "    amount_in_words TEXT,"
        		+ "    created_at      TEXT,"
        		+ "    updated_at      TEXT,"
        		+ "    FOREIGN KEY ("
        		+ "        customer_id"
        		+ "    )"
        		+ "    REFERENCES Customer (id) "
        		+ ");";

        String createInvoiceItemsTable = "CREATE TABLE IF NOT EXISTS InvoiceItem ("
        		+ "    invoice_item_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ "    invoice_id              REFERENCES Invoice (invoice_id),"
        		+ "    description     TEXT    NOT NULL,"
        		+ "    hsn             INTEGER,"
        		+ "    quantity        INTEGER,"
        		+ "    unit_price      REAL,"
        		+ "    total_price     REAL"
        		+ ");";

        String createDcItemsTable = "CREATE TABLE IF NOT EXISTS DCItem ("
        		+ "    id          INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ "    dc_item_id  INTEGER NOT NULL,"
        		+ "    dc_id       INTEGER REFERENCES DeliveryChallan (dc_id),"
        		+ "    description TEXT,"
        		+ "    hsn         INTEGER,"
        		+ "    quantity    INTEGER,"
        		+ "    remarks     TEXT"
        		+ ");";
        
        String createDcTable = "CREATE TABLE IF NOT EXISTS DeliveryChallan ("
        		+ "    id            INTEGER PRIMARY KEY AUTOINCREMENT,"
        		+ "    dc_id         INTEGER NOT NULL,"
        		+ "    customer_id   INTEGER REFERENCES Customer (id) "
        		+ "                          NOT NULL,"
        		+ "    order_date    TEXT,"
        		+ "    delivery_date TEXT,"
        		+ "    created_at    TEXT"
        		+ ");";  	
        
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createCustomerTable);
            stmt.execute(createInvoiceTable);
            stmt.execute(createInvoiceItemsTable);
            stmt.execute(createDcTable);
            stmt.execute(createDcItemsTable);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }		
	}
}
