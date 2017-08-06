/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import static impro.ImproController.d;
import static impro.Methods.createProgressDialog;
import static impro.Methods.isNumeric;
import static impro.Methods.isValidRekening;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Account;
import model.Database.SearchMethod;
import static model.Database.findMemberByAccountName;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class SearchAccountController implements Initializable {

    @FXML private TableView<Account> tableSearch;

    @FXML private TableColumn<Account,String> colIP;
    @FXML private TableColumn<Account,String> colPIN;
    @FXML private TableColumn<Account,String> colName;
    @FXML private TableColumn<Account,String> colAccountNo;


    @FXML private TextField inputField;
    @FXML private Button btnSave;
    @FXML private Button btnClear;
    @FXML private Label labelWarning;
    
    private ObservableList<Account> accounts;
    private int counter = 0;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTable();
    }    
    
    /**
     * Sets up all <tt>TableColumn</tt> and the <tt>TableView</tt>.
     */
    private void configureTable() {

        colIP.setCellValueFactory(new PropertyValueFactory<>("ip"));
        colPIN.setCellValueFactory(new PropertyValueFactory<>("pin"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAccountNo.setCellValueFactory(new PropertyValueFactory("accountNo"));

        accounts = FXCollections.observableArrayList();
        tableSearch.setItems(accounts);
    }
    
    
    @FXML
    public void keyListener(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                submitSearch();
            } catch (SQLException ex) {
                Logger.getLogger(SearchAccountController.class.getName()).log(Level.SEVERE, null, ex);
            }
            event.consume();
        }
    }
    
    /**
     * Performs a search for the input typed by the user when "Enter" key is pressed.
     * Results are displayed in the table.
     * @throws SQLException 
     */
    @FXML
    public void submitSearch() throws SQLException{
        String input = inputField.getText();
        //If is valid then it is an account number
        if (isValidRekening(input)) {
            //If true, then the account is found in the database
            
            //String accountOwner = d.getOwner(input);
            String[] accountInfo = d.getAccountInfo(input, SearchMethod.ACCOUNT_NUMBER);

            
            if (accountInfo != null) {
                ArrayList<String[]> arr = d.findMemberByAccountNo(input);
                createAccounts(input, arr, accountInfo);
//                ArrayList<Account> temp = new ArrayList<>();
//
//                //add them to tableview
//                for (String[] s : arr) {
//                    if (s[2] != null && s[2].equals(accountInfo[0])) {
//                        Account a = new Account(++counter + ".  " + s[0], s[1], s[2], input, accountInfo[1], accountInfo[2]);
//                        accounts.add(a);
//                    } else {
//                        Account a = new Account("--" + s[0], s[1], s[2], "", "", "");
//                        temp.add(a);
//                    }
//                }
//                temp.forEach((a) -> accounts.add(a));
            } else {
                labelWarning.setText("Rekening " + input + " tidak ditemukan.");
            }
        } //Else input is the owner's name
        else {
            ArrayList<ArrayList<String[]>> arr = findMemberByAccountName(input);
            
            

            for (ArrayList<String[]> subArray : arr){
//                ArrayList<Account> temp = new ArrayList<>();               
                String accountNo = subArray.get(0)[3];
                //0 = accountOwner
                //1 = bankName
                //2 = branchName
                String[] accountInfo = d.getAccountInfo(accountNo, SearchMethod.ACCOUNT_NUMBER);
                createAccounts(accountNo, subArray, accountInfo);
                
                //add them to tableview
                //s[0] = ip
                //s[1] = pin
                //s[2] = name
                //s[3] = account no
//                for (String[] s : subArray) {
//                    if (s[2] != null && s[2].equals(accountInfo[0])) {
//                        Account a = new Account(++counter + ". " + s[0], s[1], s[2], accountNo, accountInfo[1], accountInfo[2]);
//                        accounts.add(a);
//                    } else {
//                        //Account a = new Account("--" + s[0], s[1], s[2], accountNo);
//                        Account a = new Account("--" + s[0], s[1], s[2], "", "", "");
//                        temp.add(a);
//                    }
//                }
//                temp.forEach((a) -> accounts.add(a));
            }
        }
        
        inputField.clear();
    }
    
    private void createAccounts(String input, ArrayList<String[]> arr, String[] accountInfo){
        ArrayList<Account> temp = new ArrayList<>();
        for (String[] s : arr) {
                    if (s[2] != null && s[2].equals(accountInfo[0])) {
                        Account a = new Account(++counter + ".  " + s[0], s[1], s[2], input, accountInfo[1], accountInfo[2]);
                        accounts.add(a);
                    } else {
                        Account a = new Account("--" + s[0], s[1], s[2], "", "", "");
                        temp.add(a);
                    }
                }
                temp.forEach((a) -> accounts.add(a));
    }
    
    /**
     * Deletes a single selected row when "Delete" key is pressed.
     * @param event 
     */
    @FXML
    private void deleteRow(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE){
            int selectedIndex = tableSearch.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Account removed = tableSearch.getItems().get(selectedIndex);
                tableSearch.getItems().remove(selectedIndex);
            }
            event.consume();
        }
    }
    
    /**
     * Method for "Save to Excel" btnSave that creates a .xls file
     * for the search results currently shown in the table.
     */
    @FXML
    private void printTable(){
        Stage stage = createProgressDialog();
        stage.show();
        
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                writeExcel();
                return null;
            }
        };
        
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        task.setOnSucceeded((Event event) -> {
            stage.close();
        });
    }
    
    /**
     * Create a .xls file and save it in the user's Documents folder.
     * The name of the file will be in the form of [Date] followed by file name.
     */
    private void writeExcel(){
        Date date = new Date();
        SimpleDateFormat formatFolder = new SimpleDateFormat("dd-MMMM");
        SimpleDateFormat formatFile = new SimpleDateFormat("dd-MMMM HH.mm");
        
        //append the current date to filename & folder name
        String dateForFolder = formatFolder.format(date);
        String dateForFile = formatFile.format(date).replace(" ", " pukul ");
               
        //create new folder with current Date & Month as the name
        String folderPath = System.getProperty("user.home") + "\\Desktop\\" + dateForFolder;
        new File(folderPath).mkdir();
        
        //create new excel file in the new folder
        String filePath = folderPath+ "\\excel_rekening " + dateForFile +".xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        //method that populates cell with content & apply appropriate formatting to the document
        setDocumentFormat(workbook);
        try {
            //create the file
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            
            //open file
            Desktop dt = Desktop.getDesktop();
            dt.open(new File(filePath));
        } catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create the excel file.");
            alert.showAndWait();
            Logger.getLogger(SearchMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Adjusts the font size, font style and populates cells with
     * appropriate headers and values obtained from <tt>Account</tt> objects.
     * @param workbook Output Excel file
     */
    private void setDocumentFormat(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.createSheet("IMPRO");
        sheet.getPrintSetup().setLandscape(true);
        int rowNum = 0;
        
        //create Bold font with a size of 16px;
        CellStyle styleBold = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)16);
        font.setBold(true);
        styleBold.setFont(font);

        //create header row with Bold font style.
        Row rowInitial = sheet.createRow(rowNum++);

        rowInitial.createCell(0).setCellValue("IP");
        rowInitial.createCell(1).setCellValue("PIN");
        rowInitial.createCell(2).setCellValue("Name");
        rowInitial.createCell(3).setCellValue("No. rekening");
        rowInitial.createCell(4).setCellValue("Bank");
        rowInitial.createCell(5).setCellValue("Nama Cabang");
        for (Cell c : rowInitial){
            c.setCellStyle(styleBold);
        }

        //create Regular font with a size of 16px;
        CellStyle styleRegular = workbook.createCellStyle();
        styleRegular.setWrapText(true);
        Font fontRegular = workbook.createFont();
        fontRegular.setFontHeightInPoints((short) 13);
        styleRegular.setFont(fontRegular);
        
        for (Account acc: accounts) {
            //create a new row for each record
            Row row = sheet.createRow(rowNum++);
            //cell 0 = IP, cell 1 = PIN, cell 2= Name, cell 3= acc no

            String ip = acc.getIp();
            String pin = acc.getPin();
            String name = acc.getName();
            String accNo = acc.getAccountNo();
            String bankName = acc.getBankName();
            String branchName = acc.getBranchName();
     
            if (name == null) {
                name = ""; //replace null with empty string
            }

            //Determine which account is an induk
            if (isNumeric(ip.substring(0, 1))) {
                //create a space between "induk"s
                row = sheet.createRow(rowNum++);
            }

            row.createCell(0).setCellValue(ip);
            row.createCell(1).setCellValue(pin);
            row.createCell(2).setCellValue(name);
            row.createCell(3).setCellValue(accNo);
            row.createCell(4).setCellValue(bankName);
            row.createCell(5).setCellValue(branchName);
            for (Cell c : row) {
                c.setCellStyle(styleRegular);
            }
        }       
        //adjust width of the columns
        sheet.autoSizeColumn(0); 
        sheet.autoSizeColumn(1); 
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        
    }
    
    @FXML 
    private void clearTable(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Clear everything from the table?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            tableSearch.getItems().clear();
            counter = 0;
        }
    }
    
    
}
