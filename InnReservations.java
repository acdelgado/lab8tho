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

            query = "select COUNT(*) from rooms";
            pstmt = conn.prepareStatement(query);
            rset = pstmt.executeQuery();   // NO PARAMETER NEEDED
            rset.next( );
            isFull = rset.getInt("COUNT(*)") >= 10;

            query = "select COUNT(*) from reservations";
            pstmt = conn.prepareStatement(query);
            rset = pstmt.executeQuery();   // NO PARAMETER NEEDED
            rset.next( );
            isFull = isFull && rset.getInt("COUNT(*)") >= 600;

            System.out.println("Enter letter for subsystem:");
            System.out.println("A - Admin");
            System.out.println("O - Owner");
            System.out.println("G - Guest");
            System.out.println("---------");
            System.out.println("Q - Quit");
            while (inConsole) {
                switch (s.nextLine()) {
                    case "A":
                        System.out.println("Admin");
                        break;
                    case "O":
                        System.out.println("Owner");
                        break;
                    case "G":
                        System.out.println("Guest");
                        break;
                    case "Q":
                        System.out.println("Goodbye");
                        inConsole = false;
                        break;
                    default:
                        System.out.println("Invalid option");
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
} // end class