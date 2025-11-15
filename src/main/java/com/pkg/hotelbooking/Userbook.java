
package com.pkg.hotelbooking;

import java.sql.*;

public class Userbook {
    static int num_users=0;
    private int id;
    private String phone, address, name, email;
    private Connection conn;
    
    /* 
    *  Constructor
    *  Called if user exists in database. Saves the information from the database.
    */
    public Userbook(int id){
        this.id = id;
        
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        String sql = "SELECT name,address,phone,email FROM Userbook WHERE id="+id+";";
            
        try ( Statement stmt = conn.createStatement()) {
                
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                this.name=resultSet.getString("name");
                this.address=resultSet.getString("address");
                this.phone=resultSet.getString("phone");
                this.email=resultSet.getString("email");
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
              throw new Error("Problem", e);
        }
        
    }
    
    /* 
    *  Constructor
    *  Saves the new information about a user.
    */
    public Userbook(String phone, String address, String name, String email) {
        
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        if(find(phone))
            return;
            
        if( num_users == 0 ) {
            String sql = "SELECT COUNT(*) FROM Userbook;";
            
            try ( Statement stmt = conn.createStatement()) {
                
                ResultSet resultSet = stmt.executeQuery(sql);
                if (resultSet.next()) {
                    num_users = resultSet.getInt(1);
                    System.out.println("Row count: " + num_users);
                }
                resultSet.close();
                stmt.close();
            } catch (SQLException e) {
              throw new Error("Problem", e);
            }
            
            
        }
        
        num_users++;
        this.id = num_users;
        
        this.phone = phone;
        this.address = address;
        this.name = name;
        this.email = email;
    }
    
    /* Returns true if the user exists in the database based on the phone.
    *  Saves the information if the user exists.
    */
    boolean find(String phone){
        boolean exists=false;
        String sql = "SELECT * FROM Userbook WHERE phone='"+phone+"';";
            
        try ( Statement stmt = conn.createStatement()) {
                
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                exists=true;
                this.id = resultSet.getInt("id");
                this.name=resultSet.getString("name");
                this.email=resultSet.getString("email");
                this.address=resultSet.getString("address");
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
              throw new Error("Problem", e);
        }
        
        return exists;
    }
    
    
    public int getId(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
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
    
    void insert(Userbook user) {
        String sql = "INSERT INTO Userbook VALUES("+
                this.id+",'"+
                this.name+"','"+
                this.address +"','"+
                this.phone +"','" +
                this.email + "');";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Inserted user.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void update(int id,String field,String value) {
        String sql = "UPDATE Userbook SET "+field+"="+value+" WHERE id="+id+";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Updated User.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void update(int id, String phone, String address, String name, String email) {
        String sql = "UPDATE Userbook SET phone = '" + phone + "', address = '" +
                address + "', name = '" + name + "', email= '" + email +
                "' WHERE id = " + id + ";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("User Updated.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void delete(int id) {
        //deletes also all reservations for the user (DELETE CASCADE)
        String sql = "DELETE FROM Userbook WHERE id="+id+";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Deleted user.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
}
