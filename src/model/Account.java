/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author asus
 */
public class Account {

    private final SimpleStringProperty ip;
    private final SimpleStringProperty pin;
    private final SimpleStringProperty name;
    private final SimpleStringProperty accountNo;
    private final SimpleStringProperty bankName;
    private final SimpleStringProperty branchName;

    public Account(String ip, String pin, String name, String accountNo, String bankName, String branchName) {

        this.ip = new SimpleStringProperty(ip);
        this.pin = new SimpleStringProperty(pin);
        this.name = new SimpleStringProperty(name);
        this.accountNo = new SimpleStringProperty(accountNo);
        this.bankName = new SimpleStringProperty(bankName);
        this.branchName = new SimpleStringProperty(branchName);
    }
 
    
    public String getIp() {
        return ip.getValue();
    }

    public String getPin() {
        return pin.getValue();
    }

    public String getName() {
        return name.getValue();
    }

    public String getAccountNo() {
        return accountNo.getValue();
    }
    
    public String getBankName(){
        return bankName.getValue();
    }
    
    public String getBranchName(){
        return branchName.getValue();
    }
}
