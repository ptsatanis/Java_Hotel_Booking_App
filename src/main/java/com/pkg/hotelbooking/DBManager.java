
package com.pkg.hotelbooking;
import java.sql.*;

public class DBManager {
    
    /** -----------------------------------------------------**
     * Change the below variables to connect to your database */
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/HotelBooking";
    private static final String USER = "postgres";
    private static final String PASSWORD = "5525p";
    /**------------------------------------------------------**/

    private static Connection connection;

    // Private constructor to prevent instantiation
    private DBManager() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    /*SETUP: ONLY CALL ONCE TO SETUP THE DATABASE*/
    public static void setup(){
        try(Connection conn = DBManager.getConnection()) {
            createHotelTable(conn);
            createReservationTable(conn);
            createUserTable(conn);
            createRoomTable(conn);
            createManagerTable(conn);
            setupTables(conn);
            views_setup(conn);
            data_setup(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Sucess!");
            try {
                DBManager.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /********
     *Helpful functions to create tables, views and insert data
     */
    private static void createReservationTable(Connection conn) {
        String CreateReservations = "CREATE TABLE Reservation ( " +
        "id	INTEGER	NOT NULL," +
	"num_nights	INTEGER	NOT NULL," +
        "Starting_Date	DATE	NOT NULL," +
        "breakfast	BOOLEAN DEFAULT false," +       
	"FK1_ID	INTEGER	NOT NULL," +
	"FK2_ID	INTEGER	NOT NULL," +
	"FK2_FK1_ID	INTEGER	NOT NULL," +
        "PRIMARY KEY (id) );";
        
        try{
            Statement statement = conn.createStatement();

            // Execute the SQL statement to create the table
            statement.executeUpdate(CreateReservations);

            System.out.println("Reservation created successfully!");
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createUserTable(Connection conn) {
        String CreateUsers = "CREATE TABLE UserBook (" + 
	"ID	 INTEGER	NOT NULL," +
        "Name	 VARCHAR(255)	NOT NULL," +
	"Address VARCHAR(255)," +
	"Phone	 VARCHAR(255)	NOT NULL," +
        "Email	 VARCHAR(255), " +
        "PRIMARY KEY (ID)," +
        "UNIQUE (Phone) );";
        
        try{
            Statement statement = conn.createStatement();

            // Execute the SQL statement to create the table
            statement.executeUpdate(CreateUsers);

            System.out.println("User created successfully!");
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createHotelTable(Connection conn) {
        String CreateHotels = "CREATE TABLE Hotel (" + 
        "ID	INTEGER	NOT NULL," +
	"Name	VARCHAR(255) NOT NULL," +
        "City	VARCHAR(255) NOT NULL," +
        "Address VARCHAR(255)," +
        "StarRating	INTEGER," +
        "Distance	INTEGER," +
        "PRIMARY KEY (ID) );";
        
        try{
            Statement statement = conn.createStatement();

            // Execute the SQL statement to create the table
            statement.executeUpdate(CreateHotels);

            System.out.println("Hotel created successfully!");
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createRoomTable(Connection conn) {
        String CreateRooms = "CREATE TABLE Room (" + 
	"ID	INTEGER	NOT NULL," +
	"hasView	BOOLEAN," +
        "hasWifi	BOOLEAN," +
	"hasFridge	BOOLEAN," +
        "Price	INTEGER	NOT NULL," +
	"Price_with_breakfast	INTEGER," +
        "Capacity	INTEGER," +
	"FK1_ID	INTEGER	NOT NULL," +
        "PRIMARY KEY (ID, FK1_ID) );";
        
        try{
            Statement statement = conn.createStatement();

            // Execute the SQL statement to create the table
            statement.executeUpdate(CreateRooms);

            System.out.println("Room created successfully!");
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void createManagerTable(Connection conn) {
        String CreateManagers = "CREATE TABLE Manager (" + 
	"Password	VARCHAR(255)," +
	"Username	VARCHAR(255)	NOT NULL," +
	"Phone	VARCHAR(255)	NOT NULL," +
	"Name	VARCHAR(255)," +
        "Email	VARCHAR(255)," +
	"FK1_ID	INTEGER	NOT NULL," +
        "PRIMARY KEY (Username, FK1_ID)," +
        "UNIQUE (Phone) );";
        
        try{
            Statement statement = conn.createStatement();

            // Execute the SQL statement to create the table
            statement.executeUpdate(CreateManagers);

            System.out.println("Managers created successfully!");
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void setupTables(Connection conn) {
        
        String st1 = "ALTER TABLE Reservation ADD FOREIGN KEY (FK1_ID) REFERENCES "
                + "Userbook (ID) ON DELETE CASCADE ON UPDATE CASCADE;";
        
        String st2 = "ALTER TABLE Reservation ADD FOREIGN KEY (FK2_ID, FK2_FK1_ID) REFERENCES "
                + "Room (ID, FK1_ID) ON DELETE CASCADE ON UPDATE CASCADE;";
        
        String st3 = "ALTER TABLE Room ADD FOREIGN KEY (FK1_ID) REFERENCES Hotel (ID)" +
                "ON DELETE CASCADE ON UPDATE CASCADE;";
        
        String st4 = "ALTER TABLE Manager ADD FOREIGN KEY (FK1_ID) REFERENCES Hotel (ID)" +
                "ON DELETE CASCADE ON UPDATE CASCADE;";
        
        try(Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(st1);
            stmt.executeUpdate(st2);
            stmt.executeUpdate(st3);
            stmt.executeUpdate(st4);
            System.out.println("Sucess");
            stmt.close();
        }
        catch (SQLException e) {
            throw new Error("Problem", e);
        } 
    } 
    
    private static int run_statement(Connection conn,String sql){
        try ( Statement stmt = conn.createStatement();) {		      
         stmt.executeUpdate(sql);
         stmt.close();
         return 1;
        } catch (SQLException e) {
          throw new Error("Problem", e);
        } 
    }
    
    public static void data_setup(Connection conn){
       String[] sql = new String[18];
       //--DATA--
       sql[0] = "INSERT INTO Hotel VALUES(1,'Volos Palace','Volos','Xenofodos & Thrakon',5,300);";
       sql[1] = "INSERT INTO Room VALUES(1,false,true,true,100,120,2,1);";
       sql[2] = "INSERT INTO Room VALUES(2,true,true,true,150,170,3,1);";
       
       sql[3] = "INSERT INTO Userbook VALUES(1,'Agathi Kari','street 123',6943243136, 'email@gmail.com');";
       sql[4] = "INSERT INTO RESERVATION VALUES(1,4,'2024-01-13',true,1,1,1);";
       sql[5] = "INSERT INTO RESERVATION VALUES(2,3,'2024-01-15',false,1,2,1);";
       
       sql[6] = "INSERT INTO Hotel VALUES(2,'1910 Lifetsyle Hotel','Volos','Dimitriados 25',4,150)";
       sql[7] = "INSERT INTO Room VALUES(1,false,true,true,120,140,2,2);";
       sql[8] = "INSERT INTO Room VALUES(2,true,true,true,150,170,2,2);";
       
       sql[9] = "INSERT INTO Hotel VALUES(3,'Acropolis Hotel','Athens','Amfiktionos 12',3,2000)";
       sql[10] = "INSERT INTO Room VALUES(1,true,true,true,150,170,2,3);";
       sql[11] = "INSERT INTO Room VALUES(2,true,true,true,200,220,4,3);";
       
       sql[12] = "INSERT INTO Manager VALUES('123','manager1',6954321245,'Manager A','emailA@gmail.com',1)";
       sql[13] = "INSERT INTO Manager VALUES('123','manager2',6959332146,'Manager B','emailB@gmail.com',2)";
       sql[14] = "INSERT INTO Manager VALUES('123','manager3',6956524254,'Manager C','emailC@gmail.com',3)";
       
       sql[15] = "INSERT INTO Userbook VALUES(2,'user A','street 542',6943543236, 'userEmail@gmail.com');";
       sql[16] = "INSERT INTO RESERVATION VALUES(3,5,'2024-01-13',true,2,1,3);";
       sql[17] = "INSERT INTO RESERVATION VALUES(4,3,'2024-01-17',false,2,2,2);";
       //----
       
        for (String sql1 : sql) {
            run_statement(conn, sql1);
        }
    }
    
    private static void create_view(Connection conn,String query,String view_name){
        String sql = "CREATE VIEW "+view_name+" AS "+query;
        run_statement(conn,sql);
    }
    
    private static void views_setup(Connection conn){
        String view;
        
        view= "SELECT fk2_id,fk2_fk1_id,"+
              "MAX(starting_date + INTERVAL '1 DAY' * num_nights) AS booked_until "+
              "FROM reservation "+
              "GROUP BY fk2_id,fk2_fk1_id;";
        create_view(conn,view,"checkout_date");
        
        view = "SELECT u.name AS user_name, u.id AS user_id, b.starting_date, b.num_nights, r.id AS room_id, h.name AS hotel_name, " +
                "CASE WHEN b.breakfast = true THEN r.price_with_breakfast ELSE r.price END AS final_price " +
                "FROM userbook u " +
                "JOIN reservation b ON u.id = b.fk1_id " +
                "JOIN room r ON b.fk2_id = r.id AND b.fk2_fk1_id = r.fk1_id " +
                "JOIN hotel h ON h.id = r.fk1_id;";
        create_view(conn,view,"reservations_view");
        
        view = "SELECT h.id, h.name, SUM( "+
                            "CASE WHEN b.breakfast = true THEN r.price_with_breakfast ELSE r.price END " +
                            "* EXTRACT(EPOCH FROM (cd.booked_until - b.starting_date)) / 86400) AS total_revenue " +
                "FROM hotel h " +
                "JOIN room r ON h.id = r.fk1_id " +
                "JOIN reservation b ON r.id = b.fk2_id AND r.fk1_id=b.fk2_fk1_id " +
                "JOIN checkout_date cd ON b.fk2_id = cd.fk2_id " +
                "GROUP BY h.id, h.name;";
        create_view(conn,view,"hotel_revenue");

     }
    /************/
}
