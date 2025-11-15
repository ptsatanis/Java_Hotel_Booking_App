
package com.pkg.hotelbooking;

import java.sql.*;

public class HotelRoom {
    
    private Hotel hotel;
    private int id;
    private int price,capacity;
    private boolean hasView=false,hasFridge=false,hasWifi=false;
    private int pricewBr=-1;
    private Connection conn;
    
    /* 
    *  Constructor to call if room exists in database
    */
    public HotelRoom(int id,String hotel_name){
        this.id = id;
        this.hotel = new Hotel(hotel_name);
        
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        String sql = "SELECT * FROM Room WHERE id="+id+" AND fk1_id = "+this.hotel.getId()+";";
            
        try ( Statement stmt = conn.createStatement()) {
                
            ResultSet resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                this.price=resultSet.getInt("price");
                this.capacity=resultSet.getInt("capacity");
                this.hasView=resultSet.getBoolean("hasView");
                this.hasFridge=resultSet.getBoolean("hasFridge");
                this.hasWifi=resultSet.getBoolean("hasWifi");
                
                if( resultSet.getString("price_with_breakfast")==null)
                    this.pricewBr = -1;
                else
                    this.pricewBr=resultSet.getInt("price_with_breakfast");
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
              throw new Error("Problem", e);
        }
        
    }
    
    /* 
    *  Constructor to call for a new room
    */
    public HotelRoom(Hotel hotel,int price,int capacity){
        this.hotel=hotel;
        this.price=price;
        this.capacity=capacity;
        this.pricewBr = price + 20;
        
        try {
            this.conn = DBManager.getConnection();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        if( hotel.getNumRooms() == 0 ) {
            String sql = "SELECT COUNT(*) FROM Room WHERE FK1_ID = " + hotel.getId() + ";";
            
            try ( Statement stmt = conn.createStatement()) {
                
                ResultSet resultSet = stmt.executeQuery(sql);
                if (resultSet.next()) {
                    hotel.setNumRooms(resultSet.getInt(1));
                    System.out.println("Row count: " + hotel.getNumRooms());
                }
                
            } catch (SQLException e) {
              throw new Error("Problem", e);
            }
        }
        
        hotel.setNumRooms( hotel.getNumRooms()+ 1);
        this.id = hotel.getNumRooms();
        
    }
    
    public HotelRoom(Hotel hotel,int price,int capacity,boolean hasView,boolean hasFridge,boolean hasWifi){
        this(hotel,price,capacity);
        this.hasView=hasView;
        this.hasFridge=hasFridge;
        this.hasWifi=hasWifi;
    }
    
    String getPricewBr(){
        if(this.pricewBr==-1)
            return null;
        else
            return String.valueOf(this.pricewBr);
    }
    
    int getCapacity() {
        return this.capacity;
    }
    
    int getPrice() {
        return this.price;
    }
    
    /* 
    *  Insert room to the database.
    */
    void insert(HotelRoom room){
        String sql = "INSERT INTO Room VALUES("+
                room.id+","+
                room.hasView+","+
                room.hasWifi+","+
                room.hasFridge+","+
                room.price+","+
                room.pricewBr +","+
                room.capacity +","+
                room.hotel.getId()+");";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Inserted room.");
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void delete(int id,int hotel_id){
        // Also deletes all reservations for this room (DELETE CASCADE)
        String sql = "DELETE FROM Room WHERE id="+id+" AND fk1_id="+hotel_id+";";
        
        try ( Statement stmt = conn.createStatement();) {
            stmt.executeUpdate(sql);
            System.out.println("Room deleted.");
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    /*Update one value in room table*/
    void update(int id, int hotel_id,String field,String value){
        String sql = "UPDATE Room SET "+field+"="+value+" WHERE id="+id
                + " AND fk1_id = " + hotel_id +";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Room Updated.");
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    
    void update(int id, int hotel_id, int price, int capacity, boolean hasView,boolean hasFridge,boolean hasWifi) {
        String sql = "UPDATE Room SET price = " + price + ", capacity = " +
                capacity + ", hasView = " + hasView + ", hasFridge = " + hasFridge +
                ", hasWifi = " + hasWifi + ", price_with_breakfast = " + String.valueOf(price + 20) +
                " WHERE id = " + id + 
                " AND fk1_id = " + hotel_id +";";
        
        try ( Statement stmt = conn.createStatement();) {		      
            stmt.executeUpdate(sql);
            System.out.println("Room Updated.");
        } catch (SQLException e) {
          throw new Error("Problem", e);
        }
    }
    public int getId(){
        return this.id;
    }
    public boolean getView(){
        return this.hasView;
    }
    public boolean getFridge(){
        return this.hasFridge;
    }
    public boolean getWiFi(){
        return this.hasWifi;
    }
    @Override
    public String toString(){
        
        String str = "Room " + this.id;
        
        str += "\nPrice: ";
        if(this.pricewBr==-1)
            str+=this.price+"€\n";
        else
            str+=this.price+"€ (+20€ with breakfast)\n";
        
        str+="Capacity: "+capacity+" people\n";
        if(this.hasView || this.hasFridge || this.hasWifi){
            str+="The room has: ";
            if(this.hasWifi)
                str+="Wifi, ";
            if(this.hasFridge)
                str+="fridge, ";
            if(this.hasView)
                str+="view\n";
        }
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
