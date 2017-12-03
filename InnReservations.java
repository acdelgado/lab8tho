//  Sample JDBC code illustrating making a table with three tuples
//  for use in the Select and Select2 JDBC sample programs.
//  We use a PreparedStatement object.
//  E.E. Buckalew (ok, so yeah, I borrowed sections from Dr. M. Liu)
import java.io.*;
import java.util.*;
import java.sql.*;

class InnReservations {	
    public static void main (String args []) throws FileNotFoundException {
        Scanner scan = new Scanner(new File("ServerSettings.txt"));
        Console console;
        Connection conn = null;
        ResultSet rset = null;
        PreparedStatement pstmt = null;
        String query = null, line = null;
        String url = scan.nextLine();	
        String userID = scan.nextLine();
        String pword = scan.nextLine();
        int roomsSize;
        int reservationsSize;
        boolean isFull;
        boolean inConsole = true;
        Scanner s = new Scanner(System.in);
        int i = 0;

        try {
            // Load the mysql JDBC driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println ("Driver class found and loaded.");

            // now connect to mySQL with user's own database
            System.out.println ("Connecting...");
            conn =  DriverManager.getConnection(
            url + "?"
            + "user=" + userID + "&password=" + pword);
            System.out.println ("connected.");

            // now we're going to create a table and populate it
            try {
                // put our CREATE TABLE statement into a String
                String table = "CREATE TABLE rooms("
                + "id char(5) PRIMARY KEY,"
                +"name char(40),"
                +"beds integer,"
                +"bedtype char(10),"
                +"maxOccupancy integer,"
                +"basePrice float,"
                +"decor char(15));";

                // here we get our Statement and execute our CREATE TABLE statement
                Statement s1 = conn.createStatement();
                s1.executeUpdate(table);
            }
            catch (Exception ee) {
                System.out.println("ee96: " + ee);
            }
            try{
                String table2 = "CREATE TABLE reservations("
                +"id integer PRIMARY KEY,"
                +"room char(5) REFERENCES rooms(id),"
                +"checkin date,"
                +"checkout date,"
                +"rate float,"
                +"lastname char(20),"
                +"firstname char(20),"
                +"adults integer,"
                +"kids integer);";

            // here we get our Statement and execute our CREATE TABLE statement
            Statement s2 = conn.createStatement();
            s2.executeUpdate(table2);
            }
            catch (Exception ee) {
                System.out.println("ee96: " + ee);
            }
            
            while (inConsole) {
               System.out.println("\nEnter letter for subsystem:");
               System.out.println("A - Admin");
               System.out.println("O - Owner");
               System.out.println("G - Guest");
               System.out.println("---------");
               System.out.println("Q - Quit\n");
                switch (s.nextLine()) {
                    case "A":
                        boolean inAdmin = true;
                        while(inAdmin){
                           try
                           {
                           query = "select COUNT(*) from rooms";
                           pstmt = conn.prepareStatement(query);
                           rset = pstmt.executeQuery();   // NO PARAMETER NEEDED
                           rset.next( );
                           roomsSize = rset.getInt("COUNT(*)");
                           isFull = roomsSize >= 10;

                           query = "select COUNT(*) from reservations";
                           pstmt = conn.prepareStatement(query);
                           rset = pstmt.executeQuery();   // NO PARAMETER NEEDED
                           rset.next( );
                           reservationsSize = rset.getInt("COUNT(*)");
                           isFull = isFull && reservationsSize >= 600;
                           
                           System.out.println("Admin");
                           System.out.println("Status: " + (isFull ? "Full" : "Empty")); // add "no database"
                           System.out.println("Reservations: " + reservationsSize);
                           System.out.println("Rooms: " + roomsSize);
                           }
                           
                           catch(Exception e)
                           {
                              System.out.println("Admin");
                           System.out.println("Status: No Database");
                           System.out.println("Reservations: 0");
                           System.out.println("Rooms: 0");
                           }
                           
                           System.out.println("\nOptions");
                           System.out.println("V {Rooms | Reservations} - View {Rooms | Reservations} table");
                           System.out.println("C - Clear Database");
                           System.out.println("L - Load/Reload");
                           System.out.println("RM - Remove Database");
                           System.out.println("RET - Return\n");
                           switch(s.nextLine())
                           {
                              case "C":
                                 try
                                 {
                                 query = "delete from rooms;";
                                 Statement clear = conn.createStatement();
                                 clear.executeUpdate(query);
                                 
                                 query = "delete from reservations;";
                                 clear = conn.createStatement();
                                 clear.executeUpdate(query);
                                 break;
                                 }
                                 catch(Exception e){break;}
                              case "RM":
                                 try{
                                 query = "drop table rooms;";
                                 Statement rm = conn.createStatement();
                                 rm.executeUpdate(query);
                                 
                                 query = "drop table reservations;";
                                 rm = conn.createStatement();
                                 rm.executeUpdate(query);
                                 break;
                                 }
                                 catch(Exception e){break;}
                              case "V Rooms":
                                 try{
                                 query = "select * from rooms;";
                                 runAndPrint(query,conn);
                                 break;
                                 }
                                 catch(Exception e){break;}
                              case "V Reservations":
                                 try{
                                 query = "select * from reservations;";
                                 runAndPrint(query,conn);
                                 break;
                                 }
                                 catch(Exception e){break;}
                              case "RET":
                                 inAdmin = false;
                                 break;
                              default:
                                 System.out.println("Invalid option\n");
                                 break;
                           }
                        }
                        break;
                    case "O":
                        boolean inOwner = true;
                        while(inOwner){
                           System.out.print("Owner");
                           System.out.println(" Options");
                           System.out.println("O - Occupancy Overview");
                           System.out.println("REV - View Revenue");
                           System.out.println("RES - View Reservations");
                           System.out.println("ROOM - View Rooms");
                           System.out.println("RET - Return\n");
                           switch(s.nextLine())
                           {
                              case "O":
                                 System.out.print("Enter a date (format = MM-DD): ");
                                 String date = s.nextLine();
                                 query = "select name,isoccupied\n"
                                 + "from\n"
                                 + "(select distinct name,\n"
                                 + "case\n"
                                 + "when checkin <= '2010-" + date 
                                 + "' and checkout >= '2010-" + date 
                                 + "' then 'Occupied'\n"
                                 + "else 'Empty'\n"
                                 + "end\nas isoccupied\n"
                                 + "from rooms ro, reservations re\n"
                                 + "where ro.id = re.room\n"
                                 + "order by isoccupied desc) as j\n"
                                 + "group by name;";
                                 runAndPrint(query,conn);
                                 break;
                              case "REV":
                                 query = "select monthname(checkout),sum(rate*datediff(checkout,checkin))\n"
                                 + "from reservations\n"
                                 + "group by monthname(checkout)\n"
                                 + "order by month(checkout);";
                                 runAndPrint(query,conn);
                                 break;
                              case "RES":
                                 System.out.println("View Reservations: Options");
                                 System.out.println("T - Specify Time Period");
                                 System.out.println("R - Specify Room");
                                 System.out.println("TR - Specify Time Period and Room");
                                 switch(s.nextLine())
                                 {
                                    case "T":
                                       try
                                       {
                                       System.out.print("From: ");
                                       String from = s.nextLine();
                                       System.out.print("\nTo: ");
                                       String to = s.nextLine();
                                       
                                       query = "select id\n"
                                       + "from reservations\n"
                                       + "where checkin >= '2010-" + from + "'\n"
                                       + "and checkout <= '2010-" + to + "';";
                                       runAndPrint(query,conn);
                                       }
                                       catch(Exception e){
                                          System.out.println("Invalid date(s). Returning to Owner options.");
                                       }
                                       break;
                                    case "R":
                                       try
                                       {
                                       System.out.print("Enter Room Code: ");
                                       String rname = s.nextLine();
                                       
                                       query = "select id\n"
                                       + "from reservations\n"
                                       + "where room = '" + rname + "';";
                                       runAndPrint(query,conn);
                                       }
                                       catch(Exception e){
                                        System.out.println("Invalid code. Returning to Owner options.");
                                       }
                                       break;
                                    case "TR":
                                       try
                                       {
                                       System.out.print("From: ");
                                       String tfrom = s.nextLine();
                                       System.out.print("\nTo: ");
                                       String tto = s.nextLine();
                                       System.out.print("Enter Room Code: ");
                                       String trname = s.nextLine();
                                       query = "select id\n"
                                       + "from reservations\n"
                                       + "where checkin >= '2010-" + tfrom + "'\n"
                                       + "and checkout <= '2010-" + tto + "'\n"
                                       + "and room = '" + trname + "';";
                                       runAndPrint(query,conn);
                                       }
                                       catch(Exception e){
                                        System.out.println("Invalid input. Returning to Owner options.");
                                       }
                                       break;
                                 }
                                 System.out.print("Select a reservation number (OR RET to return to Owner options): ");
                                 String resNum = s.nextLine();
                                 if(resNum.equals("RET")) break;
                                 query = "select ro.name,re.* from reservations re, rooms ro\n"
                                 + "where re.id = " + resNum + " and ro.id = re.room;";
                                 runAndPrint(query,conn);
                                 break;
                              case "ROOM":
                                 System.out.println("Room Names:");
                                 query = "select name from rooms;";
                                 runAndPrint(query,conn);
                                 System.out.println("View Rooms: Options");
                                 System.out.println("1 - View Full Room Info");
                                 System.out.println("2 - View all Reservations under Room\n");
                                 switch(s.nextLine())
                                 {
                                    case "1":
                                       System.out.print("Enter Room Name: ");
                                       String rmname = s.nextLine();
                                       query = "select ro.*,sum(datediff(checkout,checkin)), "
                                       +"sum(rate*datediff(checkout,checkin))\n"
                                       + "from rooms ro, reservations re\n"
                                       + "where re.room = ro.id and ro.name = '" + rmname + "';";
                                       runAndPrint(query,conn);
                                       break;
                                    case "2":
                                       System.out.print("Enter Room Name: ");
                                       String rmname2 = s.nextLine();
                                       query = "select re.id,checkin,checkout "
                                       + "from rooms ro, reservations re\n"
                                       + "where re.room = ro.id and ro.name = '" + rmname2 + "'\n"
                                       + "order by checkin";
                                       runAndPrint(query,conn);
                                       System.out.print("Select a reservation number (OR RET to return to Owner options): ");
                                       String resNum2 = s.nextLine();
                                       if(resNum2.equals("RET")) break;
                                       query = "select ro.name,re.* from reservations re, rooms ro\n"
                                       + "where re.id = " + resNum2 + " and ro.id = re.room;";
                                       runAndPrint(query,conn);
                                 }
                                 break;
                              case "RET":
                                 inOwner = false;
                                 break;
                              default:
                                 System.out.println("Invalid option\n");
                                 break;
                           }
                        }
                        break;
                    case "G":
                     boolean inGuest = true;
                     while(inGuest)
                     {
                        System.out.println("Guest Options");
                        System.out.println("D - Room Details");
                        System.out.println("A - Check Room Availability");
                        System.out.println("P - Check Room Pricing");
                        System.out.println("R - Reserve a Room");
                        System.out.println("RET - Return\n");
                        switch(s.nextLine())
                        {
                           case "RET":
                              inGuest = false;
                              break;
                           default:
                              System.out.println("Invalid option\n");
                              break;
                        }
                     }
                     break;
                    case "Q":
                        System.out.println("Goodbye");
                        inConsole = false;
                        break;
                    default:
                        System.out.println("Invalid option\n");
                        break;
                }
            }

        } 
        catch (Exception ex) {
            ex.printStackTrace( );
        }
        finally {
            try {
                pstmt.close( );
            }
            catch (Exception e){}
            try {
                rset.close( );
            }
            catch (Exception e){}
            try {
                conn.close( );
            }
            catch (Exception e){}	
        } // end finally   	
    } // end main
    
    //RUNS A SELECT QUERY AND PRINTS ALL OF ITS CONTENT
    static void runAndPrint(String query, Connection conn) throws SQLException
    {
       PreparedStatement pstmt = conn.prepareStatement(query);
       ResultSet rset = pstmt.executeQuery();   // NO PARAMETER NEEDED
       ResultSetMetaData rsmd = rset.getMetaData();
       int columnsNumber = rsmd.getColumnCount();
       System.out.println();
       for(int k = 1; k <= columnsNumber; k++)
       {
          System.out.print(rsmd.getColumnName(k) + "\t");
       }
       System.out.println();
       while (rset.next()) {
         for (int j = 1; j <= columnsNumber; j++) {
            if (j > 1) System.out.print(",  ");
            String columnValue = rset.getString(j);
            System.out.print(columnValue + " ");
         }
         System.out.println();
       }
       System.out.println();
    }
    
} // end class
