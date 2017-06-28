/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author asus
 */
public class Database {

    private static Connection conn;
    private static PreparedStatement ps;
    private static DatabaseMetaData meta;
    private static ResultSet res;

    public Database() {
        conn = null;
        ps = null;
        res = null;
        try {
            initialize();
        } catch (SQLException ex) {
            System.out.println("ERROR CREATING DATABASE.  " + ex.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("FATAL ERROR");
            alert.setHeaderText(null);
            alert.setContentText("An error has occured in the database.");
            alert.showAndWait();
            System.exit(0);
        }

    }

    /**
     * Establish a connection to the .db file which contains information about members. 
     * If the .db file does not exist, create one in the user's Documents folder.
     * @throws SQLException 
     */
    private void initialize() throws SQLException {
        //save SQLite .db file to user's Documents
        String fileName = "improdata";
        String myDocumentPath = System.getProperty("user.home") + "\\Documents\\";
        
        String url = "jdbc:sqlite:" + myDocumentPath + fileName;

        long t1 = System.currentTimeMillis();
        
        //open a connection to the SQLite database
        conn = DriverManager.getConnection(url);
        if (conn != null) {          
            meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());           
            configureTables();
        }
        long t2 = System.currentTimeMillis();       
        System.out.println(t2-t1 + " milliseconds to initialize");       
    }
    
    private void configureTables() throws SQLException{
        // if the table "MEMBER" does not exist, create one
            if (!tableExists("member")) {
                ps = conn.prepareStatement("CREATE TABLE member "
                        + "(IP character(10) NOT NULL, "
                        + "PIN character(10) NOT NULL, "
                        + "Name varchar(99) NULL)");
                ps.execute();
                System.out.println("created table MEMBER.");
            
            //If the table "MEMBER exists
            } else {
                System.out.println("Table MEMBER already exists.");
                //do nothing
            }

            // if the table "INDUK" does not exist, create one
            if (!tableExists("induk")) {
                ps = conn.prepareStatement("CREATE TABLE induk "
                        + "(IP_INDUK character(10) NOT NULL, "
                        + "IP_KAKI character(10) NOT NULL)");
                ps.execute();
                System.out.println("Created table INDUK.");
            //If the table "INDUK" exists
            } else {
                System.out.println("Table INDUK already exists.");
            }
            if (!tableExists("history")) {
                ps = conn.prepareStatement("CREATE TABLE history "
                        + "(IP character(10) NOT NULL, "
                        + "DESCRIPTION varchar(99) NOT NULL, "
                        + "TIME TIMESTAMP NOT NULL)");
                ps.execute();
                System.out.println("Created table HSTORY.");
            } else {
                System.out.println("Table HSTORY already exists.");
            }
    }
    
    /**
     * Determines whether a table exists or not.
     * @param tableName name of table to be checked.
     * @return <tt>True</tt> if the table exists, <tt>false</tt> otherwise.
     * @throws SQLException 
     */
    private boolean tableExists(String tableName) throws SQLException{
        res = meta.getTables(null, null, tableName, null);        
        return res.next();
    }

    /**
     * Adds a new record into the SQL database
     *
     * @param member a <tt>String</tt> array containing the values for the new
     * record
     * @return <tt>True</tt> if a new record is created successfully,
     * <tt>false</tt> otherwise.
     * @throws SQLException
     */
    public boolean addMember(String[] member) throws SQLException {
        //If member 
        if (!checkMember(member[0])) {
            ps = conn.prepareStatement("INSERT INTO member (IP, PIN, Name) values (?, ?, ?)");
            ps.setString(1, member[0]);
            ps.setString(2, member[1]);
            ps.setString(3, member[2]);
            ps.executeUpdate();
            ps.clearParameters();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finds the PIN of a member from the SQL database. using the member's IP
     * number.
     *
     * @param ip IP of the member to be found
     * @return <tt>True</tt> if the IP is found in the database, <tt>false</tt>
     * otherwise.
     * @throws SQLException
     */
    public String[] findPINByIP(String ip) throws SQLException {
        //If record where IP = ip is found, return its PIN & Name
        //Else, notify the user the record is not found.
        if (checkMember(ip)) {
            return new String[]{res.getString("PIN"), res.getString("Name")};
        } else {
            return null;
        }
    }

    /**
     * Finds the PIN of a member from the SQL database, using the member's name.
     *
     * @param n the whole name or part of the name.
     * @return
     * @throws SQLException
     */
    public ArrayList<String[]> findByName(String n) throws SQLException {
        ps = conn.prepareStatement("SELECT * FROM member where Name like ?");
        ps.setString(1, "%" + n + "%");
        res = ps.executeQuery();
        ps.clearParameters();

        ArrayList<String[]> arr = new ArrayList<>();
        while (res.next()) {
            arr.add(getResultSetData());
        }

        return arr;
    }

    /**
     * Updates a record in the database. By default, the fields in the window
     * will be the old values of the record. If the user does not want to change
     * them, leaving the fields to use default values will update the record but
     * using old values. Hence, nothing happens.
     *
     * @param newIP IP obtained from fieldIP in the window.
     * @param newPIN PIN obtained from fieldPIN in the window.
     * @param newName Name obtained from fieldName in the window.
     * @param oldIP IP that is used initially to find the record.
     * @return <tt>True</tt> if the record is updated successfully,
     * <tt>false</tt> otherwise.
     * @throws SQLException
     */
    public static boolean editRecord(String newIP, String newPIN, String newName, String oldIP) throws SQLException {
        ps = conn.prepareStatement("UPDATE member "
                + "SET IP = ?, PIN = ?, Name = ? "
                + "WHERE IP = ?");
        ps.setString(1, newIP);
        ps.setString(2, newPIN);
        ps.setString(3, newName);
        ps.setString(4, oldIP);
        boolean success = ps.executeUpdate() == 1;
        ps.clearParameters();
        return success;
    }

    /**
     * Checks if the record exists in the database using IP as the SQL query clause.
     *
     * @param ip IP of the member to be checked
     * @return <tt>True</tt> if it exists, <tt>false</tt> otherwise.
     * @throws SQLException
     */
    public static boolean checkMember(String ip) throws SQLException {
        ps = conn.prepareStatement("SELECT * FROM member where IP = ?");
        ps.setString(1, ip);
        res = ps.executeQuery();
        ps.clearParameters();
        //if the record exists, res.next will return true
        return res.next();
    }
    
    /**
     * Deletes a record from the database where IP = IP entered by user.
     * @param ip IP entered by user.
     * @return true if record is successfully deleted, false otherwise.
     * @throws SQLException 
     */
    public static boolean deleteMember(String ip) throws SQLException{
        ps = conn.prepareStatement("DELETE FROM member where IP = ?");
        ps.setString(1, ip);
        boolean success = ps.executeUpdate() == 1;
        ps.clearParameters();
        //will return true if record is deleted.
        return success;
    }

    /**
     * Returns the 3 values of a record into a <tt>String</tt> array; IP, PIN and Name.
     *
     * @return a <tt>String</tt> array containing the IP, PIN and Name of a
     * record.
     * @throws SQLException
     */
    public static String[] getResultSetData() throws SQLException {
        return new String[]{res.getString("IP"), res.getString("PIN"), res.getString("Name")};
    }
}
