/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperclass;

import static impro.ImproController.d;
import static impro.Methods.makeRed;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Database.SearchMethod;

/**
 *
 * @author asus
 */
public class AccountFocusListener extends FocusListener {

    private boolean traversed;
    //The other credential. E.g. If account number is the main credential, then account name is the other credential.
    //Same applies for the other way arround. Main credential is used for focus check, whereas other credential is just 
    //the term to differentiate which one is being used and which one is not being used.
    private final TextField fieldOtherCredential; 
    private final TextField fieldBankName;
    private final TextField fieldBranchName;
    private final SearchMethod enumName;
    
    public AccountFocusListener(TextField fieldMainCredential, Label label, 
            TextField accOtherCredential, TextField bankName, TextField branchName, SearchMethod enumName){
        super(fieldMainCredential, label);
        this.traversed = false;
        this.fieldOtherCredential = accOtherCredential;
        this.fieldBankName = bankName;
        this.fieldBranchName = branchName;
        this.enumName = enumName;
    }
    
    private void declareTraversed(){
        this.traversed = true;
    }
    
    private String getInput() {
        return this.getField().getText();
    }
    
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        String input = getInput();
        
        if(newValue){
            declareTraversed();
        } else {
            if (traversed) {
                
                try {
                //[0] = always the other credential's value 
                    //[1] = bank name
                    //[2] = branch name               
                    String[] accountInfo = d.getAccountInfo(input, enumName);
                    if (accountInfo != null) {
                        fieldOtherCredential.setText(accountInfo[0]);
                        fieldBankName.setText(accountInfo[1]);
                        fieldBranchName.setText(accountInfo[2]);
                        getLabel().setText("");
                    } else {
                        switch (enumName) {
                            case ACCOUNT_NUMBER:
                                getLabel().setText("No. rek tidak ditemukan.");
                                makeRed(getLabel());
                                break;
                            case ACCOUNT_NAME:
                                getLabel().setText("Nama tidak ditemukan.");
                                makeRed(getLabel());
                                break;
                            default:
                                System.out.println("Error in AccountFocusListener");
                                break;
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(AccountFocusListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
