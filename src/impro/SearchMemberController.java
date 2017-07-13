/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import model.Member;
import static impro.ImproController.d;
import static impro.Methods.isValidIP;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import static model.Database.checkMember;
import static model.Database.findMemberByName;
import static model.Database.getMemberInfo;
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
public class SearchMemberController implements Initializable {

    @FXML private TableView<Member> tableSearch;
    @FXML private TableColumn<Member,String> colIP;
    @FXML private TableColumn<Member,String> colPIN;
    @FXML private TableColumn<Member,String> colName;
    @FXML private TableColumn<Member,String> colAccountNo;
    @FXML private TextField inputField;
   // @FXML private Button btnSubmit;
    @FXML private Button btnSave;
    @FXML private Button btnClear;
    @FXML private Label labelWarning;
    
    private ObservableList<Member> members;
    private ArrayList<String> enteredMembers;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        enteredMembers = new ArrayList<>();
        configureTable();
        configureTextField();
    }
    
    /**
     * sets <tt> inputField </tt> text to IP initially, limits the length of
     * input to 10 characters and removes the warning notifications when user
     * has been notified of it.
     */
    private void configureTextField() {
//        inputField.textProperty().addListener((final ObservableValue<? extends String> ov, final String oldValue, final String newValue) -> {
//            if (inputField.getText().length() > 10) {
//                String s = inputField.getText().substring(0, 10);
//                inputField.setText(s);
//            }
//            if (inputField.getText().length() == 9) {
//                labelWarning.setText("");
//            }
//        });

    }
    
    /**
     * populates the table columns with <tt>CellValueFactory</tt> to show
     * corresponding values.
     */
    private void configureTable() {
        colIP.setCellValueFactory(new PropertyValueFactory<>("IP"));
        colPIN.setCellValueFactory(new PropertyValueFactory<>("PIN"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAccountNo.setCellValueFactory(new PropertyValueFactory<>("accountNo"));
        
        members = FXCollections.observableArrayList();
        tableSearch.setItems(members);
    }
    
    
    /**
     * Listener for 
     * @param event 
     */
    @FXML
    public void keyListener(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            try {
                submitSearch();
            } catch (SQLException ex) {
                Logger.getLogger(SearchMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
            event.consume();
        }
    }
    
    /**
     * Finds a record in the database where the IP matches the user input
     */
    @FXML
    private void submitSearch() throws SQLException{
//        boolean dup = false;
        String userInput = inputField.getText();
        if(isValidIP(userInput)){
            try {
                //If arr is not null, the record is found in the database.
                //arr[0] = PIN, arr[1] = name
                if (checkMember(userInput)) //String[] arr = d.findPINByIP(userInput); ///needto change
                {
                    createMember(getMemberInfo());
//                    members.add(new Member(userInput, arr[1], arr[2], arr[3]));
//                    if(arr[4] != null){
//                        if (checkMember(arr[4])){
//                            String[] arr_child = getMemberInfo();
//                            members.add(new Member(">>" + arr_child[0], arr_child[1], arr_child[2], arr_child[3]));
//                        } else {
//                            members.add(new Member (">>" + arr[4], "tidak ditemukan", "", ""));
//                        }                       
//                    }
//                    if(arr[5] != null){
//                        if (checkMember(arr[5])){
//                            String[] arr_child = getMemberInfo();
//                            members.add(new Member(">>" + arr_child[0], arr_child[1], arr_child[2], arr_child[3]));
//                        } else {
//                            members.add(new Member (">>" + arr[5], "tidak ditemukan", "", ""));
//                        }     
//                    }
//                    enteredMembers.add(userInput);                   
                } else {
                    labelWarning.setText("IP not found.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(SearchMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            ArrayList<String[]> arr = findMemberByName(userInput);
            if(!arr.isEmpty()){
            for(String[] memberInfo : arr){
                createMember(memberInfo);
            }
            } else {
                labelWarning.setText("Name not found.");
            }
        }
//        for (String s : enteredMembers) {
//            if (s.equals(userInput)) {
//                dup = true;
//                break;
//            }
//        }
//        if (!dup) {
            
//            if (!(arr == null)) {
//                members.add(new Member(userInput, arr[0], arr[1]));
//                enteredMembers.add(userInput);
//                inputField.setText("IP");
//                inputField.end();
                //If arr is null, the record is not found in the database.
            
//        } else {
//            //If IP is already added to the TableView, notify the user.
//            labelWarning.setText("IP duplikat.");
//            inputField.setText("IP");
//            inputField.end();
//        }
    }

    private void createMember(String[] arr) throws SQLException {
        //[0] = IP
        //[1] = PIN
        //[2] = Name
        //[3] = Account Number
        //[4] = 1st child (null if not applicable)
        //[5] = 2nd child (null if not applicable)
        members.add(new Member(arr[0], arr[1], arr[2], arr[3]));
        if (arr[4] != null) {
            if (checkMember(arr[4])) {
                String[] arr_child = getMemberInfo();
                members.add(new Member(">>" + arr_child[0], arr_child[1], arr_child[2], arr_child[3]));
            } else {
                members.add(new Member(">>" + arr[4], "tidak ditemukan", "", ""));
            }
        }
        if (arr[5] != null) {
            if (checkMember(arr[5])) {
                String[] arr_child = getMemberInfo();
                members.add(new Member(">>" + arr_child[0], arr_child[1], arr_child[2], arr_child[3]));
            } else {
                members.add(new Member(">>" + arr[5], "tidak ditemukan", "", ""));
            }
        }
    }
    
    /**
     * Delete the currently selected row when user presses Del.
     * @param event 
     */
    @FXML
    private void deleteRow(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE){
            int selectedIndex = tableSearch.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Member removed = tableSearch.getItems().get(selectedIndex);
                tableSearch.getItems().remove(selectedIndex);
                enteredMembers.remove(removed.getIP());
            }
            event.consume();
        }
    }
    
    /**
     * prints the content of the TableView onto an A4 paper by default.
     */
    @FXML 
    private void printTable(){
        try {
        writeExcel();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create the excel file.");
            alert.showAndWait();
            Logger.getLogger(SearchMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Removes the contents of everything in the table if user clicks OK.
     */
    @FXML
    private void clearTable() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Clear everything from the table?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            tableSearch.getItems().clear();
            enteredMembers.clear();
        }
    }
    
    
    
    
   
    
    public void writeExcel() throws Exception {
        
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
        String filePath = folderPath+ "\\excel " + dateForFile +".xlsx";
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }
    
    private void setDocumentFormat(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.createSheet("IMPRO");
        int rowNum = 0;
        System.out.println("Creating excel");

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
        for (Cell c : rowInitial){
            c.setCellStyle(styleBold);
        }

        //create Regular font with a size of 16px;
        CellStyle styleRegular = workbook.createCellStyle();
        styleRegular.setWrapText(true);
        Font fontRegular = workbook.createFont();
        fontRegular.setFontHeightInPoints((short) 16);
        styleRegular.setFont(fontRegular);
        
        for (Member member : members) {
            //create a new row for each record
            Row row = sheet.createRow(rowNum++);
            //cell 0 = IP, cell 1 = PIN, cell 2= Name
            row.createCell(0).setCellValue(member.getIP());
            row.createCell(1).setCellValue(member.getPIN());
            String name = member.getName();
            if(name == null) name = "";
            //name = splitName(name, 35);
            row.createCell(2).setCellValue(name);
            for(Cell c : row){
                c.setCellStyle(styleRegular);
            }
        }
        
        //adjust width of the columns
        sheet.autoSizeColumn(0); 
        sheet.autoSizeColumn(1); 
        sheet.autoSizeColumn(2);
    }

//    private String splitName(String name, int length) {
//        //take 30
//        String newName = name;
//        if (name.length() >= length) {
//            String firstPart = name.substring(0, length - 1);
//            String secondPart = name.substring(length - 1, name.length() - 1);
//            secondPart = secondPart.trim();
//            System.out.println(firstPart);
//            System.out.println(secondPart);
//            firstPart = firstPart + "\n";
//            if(secondPart.length() > length){
//                secondPart = splitName(secondPart, length);
//            }
//            return firstPart+secondPart;
//            
//        } else {
//            return newName;
//        }
//
//    }
}
    

