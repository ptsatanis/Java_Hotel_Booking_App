
package com.pkg.hotelbooking;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class Queries {
    
    // the main query to search in order to book a hotel room
    public LinkedList<String> search(String city, int capacity, String start_date, String end_date,boolean hasView,boolean hasFridge, boolean hasWifi) {
        
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String formatted_start_date, formatted_end_date;
        
        
        LinkedList<String> result = new LinkedList();
        
        try ( Connection conn = DBManager.getConnection()) {
            
            java.util.Date date = inputDateFormat.parse(start_date);
            formatted_start_date = outputDateFormat.format(date);
            
            date = inputDateFormat.parse(end_date);
            formatted_end_date = outputDateFormat.format(date);
                  
            String sql = "SELECT h.name,r.id FROM hotel h " +
                        "JOIN room r ON h.id = r.fk1_id " +
                        "WHERE h.city = '" +
                        city + "' AND r.capacity >=" + capacity +
                        " AND NOT EXISTS (" +
                        "    SELECT 1" +
                        "    FROM reservation res" +
                        "    WHERE res.fk2_id = r.id AND res.fk2_fk1_id = h.id" +
                        "      AND (" +
                        "        ('"+ formatted_start_date +"' BETWEEN res.starting_date AND (SELECT booked_until FROM checkout_date"
                        + " WHERE fk2_id = r.id AND fk2_fk1_id = h.id AND starting_date='"+formatted_start_date+"'))" +
                        "        OR ('"+ formatted_end_date +"' BETWEEN res.starting_date AND (SELECT booked_until FROM checkout_date"
                        + " WHERE fk2_id = r.id AND fk2_fk1_id = h.id AND starting_date='"+formatted_start_date+"'))" +
                        "        OR (res.starting_date BETWEEN '"+ formatted_start_date +"' AND '"+ formatted_end_date +"')" +
                        "      )" +
                        "  );";
           try ( Statement stmt = conn.createStatement()) {
                    
                ResultSet resultSet = stmt.executeQuery(sql);
                    
                boolean isempty=true;
                while (resultSet.next()) {
                    isempty=false;
                    Hotel hotel = new Hotel(resultSet.getString("name"));
                    HotelRoom room = new HotelRoom(resultSet.getInt("id"),hotel.getName());
                    
                    boolean keep=true;
                    if(hasView && !room.getView()){
                       keep=false;
                    }
                    if(hasFridge && !room.getFridge()){
                        keep=false;
                    }
                    if(hasWifi && !room.getWiFi()){
                        keep=false; 
                    }
                    if(keep)
                        result.add(hotel.toString() + "\n"+room.toString());
                    else
                        continue;
                }
                if(isempty || result.isEmpty())
                        result.add("No results!");
                else
                        resultSet.close();
                    stmt.close();
                } catch (SQLException e) {
                    throw new Error("Problem", e);
                }
            
            conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    //Returns a string with all the reservations for a specific user
    public static String UserReservations(int user_id){
        Userbook user = new Userbook(user_id);
        String str="Reservations made by "+user.getName()+":\n";
        
        try ( Connection conn = DBManager.getConnection()) {
            String sql = "SELECT * FROM reservations_view v WHERE v.user_id="+user_id+";";
            
            try ( Statement stmt = conn.createStatement()) {
                    
                ResultSet resultSet = stmt.executeQuery(sql);
                while (resultSet.next()) {
                    str+="\nHotel: "+resultSet.getString("hotel_name");
                    str+=", room "+resultSet.getInt("room_id")+"\n";
                    str+="From "+resultSet.getString("starting_date");
                    str+=" for "+resultSet.getInt("num_nights")+" nights, ";
                    str+=resultSet.getInt("final_price")+"€ \n";
                }
                stmt.close();
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
            
            conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        return str;
    }
    
    //Checks if the given credentials for the manager are correct
    public static boolean ManagerCheckCredentials(String username,String password){
        boolean result=false;
        
        try ( Connection conn = DBManager.getConnection()) {
            String sql = "SELECT password FROM Manager WHERE username='"+username+"';";
            
            try ( Statement stmt = conn.createStatement()) {
                    
                ResultSet resultSet = stmt.executeQuery(sql);
                if (resultSet.next() && resultSet.getString("password").equals(password)) {
                    result=true;
                }
                stmt.close();
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
            conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        return result;
    }
    
    //Return a string with the hotel name, for the hotel corresponding to a specific manager
    public static String getHotelName(String username) {
        String result = null;
        try ( Connection conn = DBManager.getConnection()) {
            String sql = "SELECT h.name FROM hotel h JOIN manager m ON m.fk1_id=h.id" +
                    " WHERE m.username = '"+username+"';";
            
            try ( Statement stmt = conn.createStatement()) {
                    
                ResultSet resultSet = stmt.executeQuery(sql);
                if (resultSet.next()) {
                    result = resultSet.getString("name");
                }
                stmt.close();
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
            conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        return result;
    }
    
    //returns the number of rooms that exist in a hotel
    public static int getNumOfRooms(String hotel_name) {
        int result=-1;
        
        Hotel h = new Hotel(hotel_name);
        
        try ( Connection conn = DBManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM room WHERE fk1_id = " + h.getId() + ";";
            
            try ( Statement stmt = conn.createStatement()) {
                    
                ResultSet resultSet = stmt.executeQuery(sql);
                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }
                stmt.close();
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
            conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        h.closeConnection();
        return result;
    }
    
    // returns a list with all the room information for a hotel
    public static LinkedList<String> ViewRooms(String hotel_name){
        LinkedList<String> result=new LinkedList();
        Hotel hotel = new Hotel(hotel_name);
        
        try ( Connection conn = DBManager.getConnection()) {
            String sql = "SELECT id FROM room WHERE fk1_id = " + hotel.getId() + ";";
            
            try ( Statement stmt = conn.createStatement()) {
                    
                ResultSet resultSet = stmt.executeQuery(sql);
                while (resultSet.next()) {
                    HotelRoom room = new HotelRoom(resultSet.getInt("id"),hotel.getName());
                    result.add(room.toString());
                    room.closeConnection();
                }
                stmt.close();
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
            conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        hotel.closeConnection();
        return result;
    }
    
    //returns the total revenue for a hotel
     public static int getTotalRevenue(String hotel_name) {
        int result=0;
        
        Hotel h = new Hotel(hotel_name);
        
        try ( Connection conn = DBManager.getConnection()) {
            String sql = "SELECT total_revenue FROM hotel_revenue WHERE id = " + h.getId() + ";";
            
            try ( Statement stmt = conn.createStatement()) {
                    
                ResultSet resultSet = stmt.executeQuery(sql);
                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }
                stmt.close();
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
            conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        
        h.closeConnection();
        return result;
    }
    
    //Returns a list with all the reservations for a specific hotel
    public static LinkedList<String> ViewReservations(String hotel_name) {
        LinkedList<String> result=new LinkedList();
        Hotel hotel = new Hotel(hotel_name);
        int room_id=-1;
        boolean hasResult=false;
        
        try ( Connection conn = DBManager.getConnection()) {
            String sql = "SELECT r.id,b.starting_date,b.num_nights FROM reservation b "+
                    "JOIN room r ON fk2_id=r.id AND fk2_fk1_id=r.fk1_id WHERE r.fk1_id= " + hotel.getId() 
                    + " GROUP BY r.id,b.starting_date,b.num_nights;";
            
            try ( Statement stmt = conn.createStatement()) {
                    
                ResultSet resultSet = stmt.executeQuery(sql);
                String str="";
                while (resultSet.next()) {
                    hasResult=true;
                    int tmp=resultSet.getInt("id");
                    if(tmp!=room_id){
                        if(!str.equals(""))
                            result.add(str);
                        room_id=tmp;
                        str = "Room "+String.valueOf(room_id)+"\n";
                    }
                    str += resultSet.getString("starting_date")+" for ";
                    str += resultSet.getString("num_nights")+" nights\n";
                }
                if(!str.equals(""))
                    result.add(str);
                stmt.close();
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
            conn.close();
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
        if(!hasResult)
            result.add("No reservations!");
        
        hotel.closeConnection();
        return result;
    }
}
