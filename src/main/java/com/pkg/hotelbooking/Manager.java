package com.pkg.hotelbooking;

import java.sql.*;

public class Manager {
    private int hotel_id;
    private String name, phone, username, password, email;
    private Connection conn;
    
    /* 
    *  Constructor
    *  If Manager username exists does nothing else saves the new information.
    */
    public Manager(int hotel_id, String username, String password, String phone, String name, String email) {
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        if(find(username))
            return;
        
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.hotel_id = hotel_id;
        this.email = email;
    }
    
    /* 
    *  Constructor
    *  Called if Manager exists in the database. Gets the information from the database.
    */
    public Manager(String username) {
        
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        this.username = username;
        
        String sql = "SELECT * FROM Manager WHERE username='"+username+"';";
        
        try ( Statement stmt = conn.createStatement()) {
                
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                this.name=resultSet.getString("name");
                this.phone=resultSet.getString("phone");
                this.password=resultSet.getString("password");
                this.hotel_id = resultSet.getInt("fk1_id");
                this.email = resultSet.getString("email");
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
              throw new Error("Problem", e);
        }
    }
    
    boolean find(String username){
        boolean exists=false;
        String sql = "SELECT 1 FROM Manager WHERE username='"+username+"';";
            
        try ( Statement stmt = conn.createStatement()) {
                
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                exists=true;
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
              throw new Error("Problem", e);
        }
        
        return exists;
    }
    
    void insert(Manager manager) {
        String sql = "INSERT INTO Manager VALUES('"+
                this.password+"','"+
                this.username+"','"+
                this.phone +"','"+
                this.name + "','" +
                this.email + "'," +
                this.hotel_id + ");";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Inserted manager.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void update(String username,String field,String value) {
        String sql = "UPDATE Manager SET "+field+"='"+value+"' WHERE username='"+username+"';";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Updated Manager.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void update(String password, String username, String phone, String name, String email) {
        String sql = "UPDATE Manager SET phone = '" + phone + "', password = '" +
                password + "', name = '" + name + "', email = '" + email +
                "' WHERE username = " + username + ";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Manager Updated.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void delete(String username) {
        String sql = "DELETE FROM Manager WHERE username='"+username+"';";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Deleted Manager.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    String getId() {
        return this.username;
    }
    
    // Always call at end of operations
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
