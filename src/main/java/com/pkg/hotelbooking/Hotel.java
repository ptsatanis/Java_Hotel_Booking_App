
package com.pkg.hotelbooking;
import java.sql.*;

public class Hotel {
    
    static int num_hotels=0;
    private int id;
    private String name,city,address;
    private int starrating=-1,distance=-1;
    private int num_rooms=0;
    private Connection conn;
    
    /*Returns true if hotel with specific name exists else false.*/
    boolean find(String name){
        boolean exists=false;
        String sql = "SELECT * FROM Hotel WHERE name='"+name+"';";
            
        try ( Statement stmt = conn.createStatement()) {
                
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                exists=true;
                this.id = resultSet.getInt("id");
                this.starrating=resultSet.getInt("starrating");
                this.distance=resultSet.getInt("distance");
                this.city=resultSet.getString("city");
                this.address=resultSet.getString("address");
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
              throw new Error("Problem", e);
        }
        
        return exists;
    }
    
    /* Constructor for a Hotel object.
    *  If hotel exists gets the information from database
    *  else saves the hotel with a new id.
    */
    public Hotel(String name){
        this.name=name;
        
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        boolean exists=find(name);
        if(exists)
            return;
        
        if( num_hotels == 0 ) {
            String sql = "SELECT COUNT(*) FROM Hotel;";
            
            try ( Statement stmt = conn.createStatement()) {
                
                ResultSet resultSet = stmt.executeQuery(sql);
                if (resultSet.next()) {
                    num_hotels = resultSet.getInt(1);
                    System.out.println("Row count: " + num_hotels);
                }
                resultSet.close();
                stmt.close();
            } catch (SQLException e) {
              throw new Error("Problem", e);
            }
        }
        num_hotels++;
        this.id = num_hotels;
    }
    
    public Hotel(String name,String city,String address,int starrating,int distance){
        this(name);
        this.starrating=starrating;
        this.distance=distance;
        this.city = city;
        this.address=address;
    }
    
    String getStarRating(){
        if(this.starrating==-1)
            return null;
        else
            return String.valueOf(this.starrating);
    }
    
    String getDistance(){
        if(this.distance==-1)
            return null;
        else
            return String.valueOf(this.distance);
    }
    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public int getNumRooms(){
        return this.num_rooms;
    }
    
    public void setNumRooms(int num_rooms){
        this.num_rooms = num_rooms;
    }
    
    /*Insert hotel in the database*/
    void insert(Hotel hotel){
        String sql = "INSERT INTO Hotel VALUES("+
                hotel.id+", '"+
                hotel.name+"' ,'"+
                hotel.city+"' ,'"+
                hotel.address+"' ,"+
                hotel.getStarRating()+","+
                hotel.getDistance()+");";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Inserted hotel.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    /*Update one value in Hotel table*/
    void update(int id,String field,String value){
        String sql = "UPDATE Hotel SET "+field+"="+value+" WHERE id="+id+";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Updated hotel.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    /*Update all fields of Hotel row*/
    void update(int id,String name,String city,String address,int starrating,int distance){
        String sql = "UPDATE Hotel SET "+
                "name = "+name+
                ",city = "+city+
                ",address = "+address+
                ",starrating = "+starrating+
                ",distance = "+distance+
                " WHERE id="+id+";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Updated hotel.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void delete(int id){
        //deletes also all rooms, the manager and all reservations connected to this hotel (DELETE CASCADE)
        String sql = "DELETE FROM Hotel WHERE id="+id+";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Deleted hotel.");
            stmt.close();
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    @Override
    public String toString(){
        char c='\u2605';
        String str = this.name+"  ";
        for(int i=0;i<this.starrating;i++)
            str+=c;
        str+=" \n"+this.city+", "+this.address+", "+this.distance+"m from center.";
        return str;
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
