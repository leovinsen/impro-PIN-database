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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javafx.collections.ObservableList;
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

    public enum SearchMethod{
        ACCOUNT_NAME, ACCOUNT_NUMBER
    }
    
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
    public boolean addMember(String[] member, boolean induk) throws SQLException {
        //[0] = IP, 
        //[1] = PIN, 
        //[2] = NAME,
        //[3] = ACCOUNT_NUM
        if (!checkMember(member[0])) {
            ps = conn.prepareStatement("INSERT INTO member (IP, PIN, NAME, ACCOUNT_NUM, INDUK) values (?, ?, ?, ?, ?)");
            ps.setString(1, member[0]);
            ps.setString(2, member[1]);
            ps.setString(3, member[2]);
            ps.setString(4, member[3]);
            ps.setBoolean(5, induk);
            ps.executeUpdate();
            ps.clearParameters();
            return true;
        } else {
            return false;
        }
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
     * @param newRekening
     * @param newInduk
     * @param oldIP IP that is used initially to find the record.
     * @return <tt>True</tt> if the record is updated successfully,
     * <tt>false</tt> otherwise.
     * @throws SQLException
     */
    public static boolean editMemberRecord(String newIP, String newPIN, String newName,
            String newRekening, boolean newInduk, String oldIP) throws SQLException {
        
        ps = conn.prepareStatement("UPDATE member "
                + "SET IP = ?, PIN = ?, Name = ?, ACCOUNT_NUM = ?, Induk = ? "
                + "WHERE IP = ?");
        ps.setString(1, newIP);
        ps.setString(2, newPIN);
        ps.setString(3, newName);
        ps.setString(4, newRekening);
        ps.setBoolean(5, newInduk);
        ps.setString(6, oldIP);
        boolean success = ps.executeUpdate() == 1;
        ps.clearParameters();
        return success;
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
        deleteChild(ip, ""); 
        //returns true if record is deleted.
        return success;
    }
    
    public void addChild(String induk, String child) throws SQLException {
        //
        ps = conn.prepareStatement("INSERT INTO induk (IP_INDUK, IP_KAKI) values (?, ?)");
        ps.setString(1, induk);
        ps.setString(2, child);
        ps.executeUpdate();
        ps.clearParameters();
        System.out.println("child added");
    }

    
    public static boolean editChild(String oldIndukIP, String newIndukIP, String oldChildIP, String newChildIP) throws SQLException{
        ps = conn.prepareStatement("UPDATE induk "
                + "SET IP_INDUK = ?, IP_KAKI = ? "
                + "WHERE IP_INDUK = ? AND IP_KAKI = ?");
        ps.setString(1, newIndukIP);
        ps.setString(2, newChildIP);
        ps.setString(3, oldIndukIP);
        ps.setString(4, oldChildIP);
        boolean success = ps.executeUpdate() == 1;
        ps.clearParameters();
        return success;
    }
    

    public static boolean deleteChild(String indukIP, String childIP) throws SQLException{
        if (!childIP.isEmpty()) {
            ps = conn.prepareStatement("DELETE FROM induk where IP_INDUK = ? AND IP_KAKI = ?");
            ps.setString(1, indukIP);
            ps.setString(2, childIP);
        } else {
            ps = conn.prepareStatement("DELETE FROM induk where IP_INDUK =?");
            ps.setString(1, indukIP);
        }
        boolean success = ps.executeUpdate() == 1;
        ps.clearParameters();
        //returns true if record is deleted.
        return success;
    }

    public void insertRekeningHistory(String ip, String oldRekening, String newRekening, String owner) throws SQLException{
        ps = conn.prepareStatement("INSERT INTO HISTORY (IP, ACCOUNT_NUM, OWNER, TIME) values ( ?, ?, ?, ?)");
        ps.setString(1, ip);
        
        String description;
        //User fills in acc number for a member without acc number
        if(oldRekening == null && newRekening != null){
            ps.setString(2, newRekening);          
        }
        //User removes account number from a member
        else if ( oldRekening != null && newRekening == null){
            description = "DIHAPUS";
            ps.setString(2, description);
        }
        //User modifies the account number of a member
        else if ((oldRekening != null && newRekening !=null) && !oldRekening.equals(newRekening)){
            ps.setString(2, newRekening);
        } 
        //acc number stays the same
        else {
            ps.clearParameters();
            return;
        }
        ps.setString(3, owner);     
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ps.setTimestamp(4, timestamp);
        ps.executeUpdate();
        ps.clearParameters();
    }
    
    public boolean findHistory(String ip, ObservableList<Record> records) throws SQLException {
        records.clear();
        ps = conn.prepareStatement("SELECT ACCOUNT_NUM, OWNER, TIME FROM history where IP = ?");
        ps.setString(1, ip);
        res = ps.executeQuery();
        while(res.next()){
            String accNo= res.getString("ACCOUNT_NUM");
            String owner = res.getString("OWNER");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy,HH:mm");
            Timestamp ts = res.getTimestamp("time");
            String datetime = sdf.format(ts);
            Record record = new Record(accNo, owner, datetime);
            records.add(record); 
        }       
        ps.clearParameters();
        return !records.isEmpty();
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
    
    public static ArrayList<String[]> findMemberByName(String name) throws SQLException {
        ArrayList<String[]> arr = new ArrayList<>();
        ps = conn.prepareStatement("SELECT * FROM member where NAME = ?");
        ps.setString(1, name);
        res = ps.executeQuery();
        ps.clearParameters();
        //if the record exists, res.next will return true
        while(res.next()){
            String[] memberInfo = getMemberInfo();
            arr.add(memberInfo);
        }
        return arr;
    }

    
    
    public boolean checkAccount(String param, SearchMethod sm) throws SQLException{
        String whereClause = "";
        switch (sm){
            case ACCOUNT_NUMBER:
                whereClause = "ACCOUNT_NUM = ?";
                break;
            case ACCOUNT_NAME:
                whereClause = "OWNER = ?";
                break;
            default:
                System.out.println("checkAccount wrong enum.");
        }
        ps = conn.prepareStatement("SELECT * FROM bank_account WHERE " + whereClause);
        ps.setString(1, param);
        res = ps.executeQuery();
        ps.clearParameters();
        return res.next();
    }
    
    
    
    public int countAccountUsage(String num) throws SQLException{
        ps = conn.prepareStatement("SELECT COUNT(ACCOUNT_NUM) FROM MEMBER WHERE ACCOUNT_NUM = ?");
        ps.setString(1, num);
        ResultSet res= ps.executeQuery();
        ps.clearParameters();
        return res.getInt(1);     
    }
    
    public void addAccountDetails(String num, String name, String bankName, String branchName) throws SQLException{
        ps = conn.prepareStatement("INSERT INTO bank_account (OWNER, ACCOUNT_NUM, BANK_NAME, BRANCH_NAME) "
                + "values (?, ?, ?, ?)");
        ps.setString(1, name);
        ps.setString(2, num);
        ps.setString(3, bankName);
        ps.setString(4, branchName);
        ps.executeUpdate();
        ps.clearParameters();
    }
    
    /**
     * Returns a whole record from table MEMBER, which previously has been selected by
     * checkMember(). Note: This function must be called after checkMember() to work.
     *
     * @return a <tt>String</tt> array containing the IP, PIN and Name of a
     * record.
     * @throws SQLException
     */
    public static String[] getMemberInfo() throws SQLException {
        //If the member has induk
        
        String ip = res.getString("IP");
        String pin = res.getString("PIN");
        String name = res.getString("NAME");
        String rekening = res.getString("ACCOUNT_NUM");
        boolean induk = res.getBoolean("INDUK");
        String child1 = null;
        String child2 = null;
        if(induk){
            ps = conn.prepareStatement("SELECT IP_KAKI FROM induk where IP_INDUK = ?");
            ps.setString(1, ip);
            res = ps.executeQuery();
            if(res.next()){
                child1 = res.getString("IP_KAKI");
            }
            if(res.next()){
                child2 = res.getString("IP_KAKI");
            }
        }
        ps.clearParameters();
        return new String[]{ip, pin, name, rekening, child1, child2};
    }
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    public String[] getAccountInfo(String param, SearchMethod enumName) throws SQLException{
        if(checkAccount(param, enumName)){
            String owner = res.getString("OWNER");
            String accountNo = res.getString("ACCOUNT_NUM");
            String bankName = res.getString("BANK_NAME");
            String branchName = res.getString("BRANCH_NAME");
            switch (enumName){
                case ACCOUNT_NUMBER:
                    return new String[]{owner, bankName, branchName};
                case ACCOUNT_NAME:
                    return new String[]{accountNo, bankName, branchName};
            }
        } 
        //If not found, return null;
        return null;
    }
}

