
package com.pkg.hotelbooking;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reservation {
    private int id, user_id, hotel_id, room_id;
    private String starting_date;
    private int num_nights;
    static int num_reservations=0;
    private boolean breakfast=false;
    private Connection conn;
    
    /* 
    *  Constructor
    *  Called if reservation exists. Saves the information from the database.
    */
    public Reservation(int id){
        
        this.id = id;
        
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        String sql = "SELECT * FROM Userbook WHERE id="+id+";";
            
        try ( Statement stmt = conn.createStatement()) {
                
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                this.starting_date=resultSet.getString("starting_date");
                this.num_nights=resultSet.getInt("num_nights");
                this.user_id=resultSet.getInt("fk1_id");
                this.room_id=resultSet.getInt("fk2_id");
                this.hotel_id=resultSet.getInt("fk2_fk1_id");
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
              throw new Error("Problem", e);
        }
    }
    
    /* 
    *  Constructor
    *  Saves the new information about a reservation.
    */
    public Reservation(int user_id, int room_id, int hotel_id, String Starting_date, int num_nights,boolean breakfast) {
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        if( num_reservations == 0 ) {
            String sql = "SELECT COUNT(*) FROM Reservation;";
            
            try ( Statement stmt = conn.createStatement()) {
                
                ResultSet resultSet = stmt.executeQuery(sql);
                if (resultSet.next()) {
                    num_reservations = resultSet.getInt(1);
                }
                resultSet.close();
                stmt.close();
            } catch (SQLException e) {
              throw new Error("Problem", e);
            }
            
        }
        
        num_reservations++;
        this.id = num_reservations;
        
        this.user_id = user_id;
        this.room_id = room_id;
        this.hotel_id = hotel_id;
        this.num_nights = num_nights;
        this.breakfast=breakfast;
        
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date date;
        try {
            date = inputDateFormat.parse(Starting_date);
            this.starting_date = outputDateFormat.format(date);
        } catch (ParseException ex) {
            Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public int getId(){
        return this.id;
    }
    
    void insert(Reservation r) {
        String sql = "INSERT INTO Reservation VALUES("+
                r.id+"," +
                r.num_nights+",'" +
                r.starting_date +"'," +
                r.breakfast+","+
                r.user_id + "," +
                r.room_id + "," +
                r.hotel_id + ");";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Inserted reservation.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void update(int id,String field,String value) {
        String sql = "UPDATE Reservation SET "+field+"="+value+" WHERE id="+id+";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Updated reservation.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void update(int id, int user_id, int room_id, int hotel_id, int num_nights, String Starting_date,boolean breakfast) {
        String sql = "UPDATE Reservation SET fk1_id = " + user_id + ", fk2_id = " +
                room_id + ", fk2_fk1_id = " + hotel_id +
                ", num_nights = " + num_nights +
                ", starting_date = '" + Starting_date +
                ", breakfast = "+breakfast+
                "' WHERE id = " + id + ";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Reservation Updated.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void delete(int id) {
        String sql = "DELETE FROM Reservation WHERE id="+id+";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Deleted reservation.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
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
