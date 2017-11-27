/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Model;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author jefferson
 */
public class Database {
    private Connection connection;
	
	public Database() {}

	public boolean connect() {
		try {
                    connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/superdatashow_db", "root", "root");
                    System.out.println("Connected!");

                    return true;
		} catch (SQLException ex) {
			System.out.println("Error connecting database: " + ex.getMessage());
		}
		return false;
	}
        
        public boolean disconnect() throws SQLException {
            try {
                connection.close();
                System.out.println("Disconnected!");
                return true;
            } catch (SQLException e) {
                System.out.println("[Model - Database] Error: " + e.getMessage());
            }
            return false;
        }
	
	public Connection getConnection() {
            return connection;
	}
}
